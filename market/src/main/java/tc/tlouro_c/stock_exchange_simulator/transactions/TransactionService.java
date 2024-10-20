package tc.tlouro_c.stock_exchange_simulator.transactions;

import java.nio.ByteBuffer;

import tc.tlouro_c.stock_exchange_simulator.FixRequest;
import tc.tlouro_c.stock_exchange_simulator.MarketController;
import tc.tlouro_c.stock_exchange_simulator.stocks.StockDAO;
import tc.tlouro_c.stock_exchange_simulator.transactions.handlers.CheckStockAvailability;
import tc.tlouro_c.stock_exchange_simulator.transactions.handlers.EnqueueResponse;
import tc.tlouro_c.stock_exchange_simulator.transactions.handlers.ExecuteTransaction;
import tc.tlouro_c.stock_exchange_simulator.transactions.handlers.RegisterTransaction;
import tc.tlouro_c.stock_exchange_simulator.transactions.handlers.TransactionHandler;
import tc.tlouro_c.stock_exchange_simulator.transactions.handlers.TransactionHandler.TransactionAcceptedException;
import tc.tlouro_c.stock_exchange_simulator.transactions.handlers.TransactionHandler.TransactionRejectedException;
import tc.tlouro_c.stock_exchange_simulator.transactions.handlers.ValidateFixRequest;
import tc.tlouro_c.stock_exchange_simulator.transactions.handlers.ValidateTransactionFields;

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
		var originalFixRequest = new FixRequest(buffer);
		var transactionProcessChain = buildTransactionProcessChain();
		Transaction transaction = null;

		System.out.println("Starting transaction processing chain");
		
		try {
			transactionProcessChain.handleTransaction(null, originalFixRequest);
		} catch (TransactionRejectedException e) {
			e.printStackTrace();
			System.out.println("Transaction processing chain finished : transaction rejected");
			transaction = e.getTransaction();
			System.out.println(transaction != null ? transaction.toString() : "Transaction is null");
			transaction.setTransactionState(TransactionState.REJECTED);
		} catch (TransactionAcceptedException e) {
			System.out.println(e.getMessage());
			System.out.println("Transaction processing chain finished : transaction accepted");
			transaction = e.getTransaction();
			System.out.println(transaction != null ? transaction.toString() : "Transaction is null");
			transaction.setTransactionState(TransactionState.EXECUTED);
		}
		System.out.println("Starting after transaction processing chain");
		var afterTransactionProcessChain = buildAfterTransactionProcessChain();
		try {
			afterTransactionProcessChain.handleTransaction(transaction, marketController);
			marketController.increaseTransactionsCount();
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

		return validateFixRequest;
	}

	private TransactionHandler buildAfterTransactionProcessChain() {
		var registerTransaction = new RegisterTransaction();
		var enqueueResponse = new EnqueueResponse();
		registerTransaction.setNextHandler(enqueueResponse);

		return registerTransaction;
	}
	
}
