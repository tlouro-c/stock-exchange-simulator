package tc.tlouro_c.stock_exchange_simulator.market;

import tc.tlouro_c.stock_exchange_simulator.Connection;

public class Market {

	private static String id = "undefined";
	private static Connection connection = Connection.UNSET;

	public static Connection getConnection() {
		return connection;
	}

	public static void setConnection(Connection connection) {
		Market.connection = connection;
	}

	public static void setId(String assignedId) {
		id = assignedId;
	}

	public static String getId() {
		return id;
	}

    public static void main(String[] args) {
        var marketController = new MarketController();

		marketController.startListening(8001);
    }
}
