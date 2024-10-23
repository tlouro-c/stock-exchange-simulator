package tc.tlouro_c.stock_exchange_simulator.broker.orders;

public enum OrderState {
	NEW(0), EXECUTED(1), REJECTED(2);

	private int value;

	private OrderState(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public static OrderState fromValue(int value) {
		if (value == 1) {
			return OrderState.EXECUTED;
		} else if (value == 2) {
			return OrderState.REJECTED;
		} else if (value == 0) {
			return OrderState.NEW;
		}
		return null;
	}
}
