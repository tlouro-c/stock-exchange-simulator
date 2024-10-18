package tc.tlouro_c.stock_exchange_simulator.transactions;

public enum TransactionType {
	BUY(1), SELL(2);

	private int value;

	private TransactionType(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
}
