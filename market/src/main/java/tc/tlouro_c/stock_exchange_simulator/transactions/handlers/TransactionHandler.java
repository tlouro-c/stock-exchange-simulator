package tc.tlouro_c.stock_exchange_simulator.transactions.handlers;

import tc.tlouro_c.stock_exchange_simulator.transactions.Transaction;

public abstract class TransactionHandler {

	protected TransactionHandler nextHandler;
	protected TransactionHandler previousHandler;

	public void setNextHandler(TransactionHandler nextHandler) {
		this.nextHandler = nextHandler;
	}

	public abstract void handleTransaction(Transaction transaction, 
		Object extra) throws TransactionRejectedException, TransactionAcceptedException;


	public class TransactionRejectedException extends Exception {

		private Transaction transaction;

		public TransactionRejectedException() {
			super("Transaction rejected");
		}

		public TransactionRejectedException(Transaction transaction) {
			super("Transaction rejected");
			this.transaction = transaction;
		}

		public TransactionRejectedException(String message, Transaction transaction) {
			super(message);
			this.transaction = transaction;
		}

		public Transaction getTransaction() {
			return transaction;
		}
	}

	public class TransactionAcceptedException extends Exception {

		private Transaction transaction;

		public TransactionAcceptedException() {
			super("Transaction accepted");
		}

		public TransactionAcceptedException(Transaction transaction) {
			super("Transaction accepted");
			this.transaction = transaction;
		}

		public TransactionAcceptedException(String message, Transaction transaction) {
			super(message);
			this.transaction = transaction;
		}

		public Transaction getTransaction() {
			return transaction;
		}
	}

	public void setPreviousHandler(TransactionHandler previousHandler) {
		this.previousHandler = previousHandler;
	}
	
}
