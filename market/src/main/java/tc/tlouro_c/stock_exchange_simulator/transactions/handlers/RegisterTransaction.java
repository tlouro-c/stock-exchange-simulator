package tc.tlouro_c.stock_exchange_simulator.transactions.handlers;

import tc.tlouro_c.stock_exchange_simulator.transactions.Transaction;
import tc.tlouro_c.stock_exchange_simulator.transactions.TransactionDAO;

public class RegisterTransaction extends TransactionHandler {
	

	@Override
	public void handleTransaction(Transaction transaction, Object extra)
			throws TransactionRejectedException, TransactionAcceptedException {

		var transactionDAO = TransactionDAO.getInstance();
		transactionDAO.insertTransaction(transaction);

		nextHandler.handleTransaction(transaction, extra);
	}
}
