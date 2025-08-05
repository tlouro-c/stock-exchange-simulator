package tc.tlouro_c.stock_exchange_simulator.market.transactions;

import java.nio.ByteBuffer;

import tc.tlouro_c.stock_exchange_simulator.FixRequest;
import tc.tlouro_c.stock_exchange_simulator.market.MarketController;
import tc.tlouro_c.stock_exchange_simulator.market.stocks.StockDAO;
import tc.tlouro_c.stock_exchange_simulator.market.transactions.handlers.CheckStockAvailability;
import tc.tlouro_c.stock_exchange_simulator.market.transactions.handlers.EnqueueResponse;
import tc.tlouro_c.stock_exchange_simulator.market.transactions.handlers.ExecuteTransaction;
import tc.tlouro_c.stock_exchange_simulator.market.transactions.handlers.RegisterTransaction;
import tc.tlouro_c.stock_exchange_simulator.market.transactions.handlers.TransactionHandler;
import tc.tlouro_c.stock_exchange_simulator.market.transactions.handlers.TransactionHandler.TransactionAcceptedException;
import tc.tlouro_c.stock_exchange_simulator.market.transactions.handlers.TransactionHandler.TransactionRejectedException;
import tc.tlouro_c.stock_exchange_simulator.market.transactions.handlers.ValidateFixRequest;
import tc.tlouro_c.stock_exchange_simulator.market.transactions.handlers.ValidateTransactionFields;

public class TransactionService {

	private static TransactionService instance;

	private TransactionService() {

	}

	public static TransactionService getInstance() {
		if (instance == null) {
			synchronized(TransactionService.class) {
				if (instance == null) {
					instance = new TransactionService();
				}
			}
		}
		return instance;
	}

	public void init() {
		var stockDAO = StockDAO.getInstance();
		stockDAO.initializeTable();
		stockDAO.populateTable();
		TransactionDAO.getInstance().initializeTable();
	}

	public void handleTransaction(MarketController marketController, ByteBuffer buffer) {
		marketController.getMarketView().processingTransactionMessage(buffer);
		var originalFixRequest = new FixRequest(buffer);
		var transactionProcessChain = buildTransactionProcessChain();
		Transaction transaction = null;
		
		try {
			transactionProcessChain.handleTransaction(null, originalFixRequest);
		} catch (TransactionRejectedException e) {
			transaction = e.getTransaction();
			transaction.setTransactionState(TransactionState.REJECTED);
		} catch (TransactionAcceptedException e) {
			transaction = e.getTransaction();
			transaction.setTransactionState(TransactionState.EXECUTED);
		}
		var afterTransactionProcessChain = buildAfterTransactionProcessChain();
		try {
			afterTransactionProcessChain.handleTransaction(transaction, marketController);
			marketController.getMarketView().transactionProcessedMessage(transaction.getTransactionState());
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}

	private TransactionHandler buildTransactionProcessChain() {
		var validateFixRequest = new ValidateFixRequest();
		var validateTransactionFields = new ValidateTransactionFields();
		var checkStockAvailability = new CheckStockAvailability();
		var executeTransaction = new ExecuteTransaction();
		validateFixRequest.setNextHandler(validateTransactionFields);
		validateTransactionFields.setNextHandler(checkStockAvailability);
		checkStockAvailability.setNextHandler(executeTransaction);
		executeTransaction.setPreviousHandler(checkStockAvailability);

		return validateFixRequest;
	}

	private TransactionHandler buildAfterTransactionProcessChain() {
		var registerTransaction = new RegisterTransaction();
		var enqueueResponse = new EnqueueResponse();
		registerTransaction.setNextHandler(enqueueResponse);

		return registerTransaction;
	}
	
}
