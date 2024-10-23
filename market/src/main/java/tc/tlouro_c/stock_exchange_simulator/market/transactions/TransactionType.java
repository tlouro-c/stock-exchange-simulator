package tc.tlouro_c.stock_exchange_simulator.market.transactions;

public enum TransactionType {
	INVALID(0), BUY(1), SELL(2);

	private int value;

	private TransactionType(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public static TransactionType valueOf(int value) {
		if (value == 1) {
			return BUY;
		} else if (value == 2) {
			return SELL;
		} else {
			return INVALID;
		}
	}
}
