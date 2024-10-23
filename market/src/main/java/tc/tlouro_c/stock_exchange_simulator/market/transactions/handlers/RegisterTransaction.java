package tc.tlouro_c.stock_exchange_simulator.market.transactions.handlers;

import tc.tlouro_c.stock_exchange_simulator.market.transactions.Transaction;
import tc.tlouro_c.stock_exchange_simulator.market.transactions.TransactionDAO;

public class RegisterTransaction extends TransactionHandler {
	

	@Override
	public void handleTransaction(Transaction transaction, Object extra)
			throws TransactionRejectedException, TransactionAcceptedException {

		var transactionDAO = TransactionDAO.getInstance();
		transactionDAO.insertTransaction(transaction);

		nextHandler.handleTransaction(transaction, extra);
	}
}
