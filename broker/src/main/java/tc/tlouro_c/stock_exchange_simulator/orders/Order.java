package tc.tlouro_c.stock_exchange_simulator.orders;

public class Order {

	private static int ordersCount = 0;

	private int id;
	private String instrument;
	private String market;
	private double limitPricePerShare;
	private int shares;
	private int side;
	
}
