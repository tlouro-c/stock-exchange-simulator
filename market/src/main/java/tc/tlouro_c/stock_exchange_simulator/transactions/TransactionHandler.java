package tc.tlouro_c.stock_exchange_simulator.transactions;

public abstract class TransactionHandler {

	protected TransactionHandler nextHandler;

	public TransactionHandler setNextHandler(TransactionHandler nextHandler) {
		this.nextHandler = nextHandler;
		return this;
	}

	public abstract void handleTransaction(Transaction transaction);
	
}
