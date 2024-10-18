package tc.tlouro_c.stock_exchange_simulator.transactions;

public enum TransactionState {
	NEW(0), EXECUTED(1), REJECTED(2);

	private int value;

	private TransactionState(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
}
