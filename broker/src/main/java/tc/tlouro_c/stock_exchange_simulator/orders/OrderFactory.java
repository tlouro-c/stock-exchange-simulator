package tc.tlouro_c.stock_exchange_simulator.orders;

public class OrderFactory {

	private static OrderFactory instance;

	private OrderFactory() {}

	public static OrderFactory getInstance() {
		if (instance == null) {
			synchronized (OrderFactory.class) {
				if (instance == null) {
					instance = new OrderFactory();
				}
			}
		}
		return instance;
	}

	public Order newOrder(String side) {
		if (side == "BUY") {
			return new BuyOrder();
		} else if (side == "SELL") {
			return new SellOrder();
		}
		return null;
	}
	
}
