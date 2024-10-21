package tc.tlouro_c.stock_exchange_simulator;

public class Broker {

	private static String id = "undefined";

	public static String getId() {
		return id;
	}
	
	public static void setId(String assignedId) {
		id = assignedId;
	}

    public static void main(String[] args) {
        System.out.println("Hello from broker!");
    }

}
