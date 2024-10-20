package tc.tlouro_c.stock_exchange_simulator;

public class Market {

	private static String id = "undefined";

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
