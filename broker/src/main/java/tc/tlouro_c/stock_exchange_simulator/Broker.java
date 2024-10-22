package tc.tlouro_c.stock_exchange_simulator;

import com.google.common.util.concurrent.AtomicDouble;

import tc.tlouro_c.stock_exchange_simulator.orders.Order;
import tc.tlouro_c.stock_exchange_simulator.orders.OrderService;
import tc.tlouro_c.stock_exchange_simulator.strategies.TradingAlgorithmOne;

import java.util.concurrent.Executors;

public class Broker {

	private static String id = "undefined";
	private static AtomicDouble balance;
	private static double initialBalance;
	private static Connection connection = Connection.UNSET;

	public static double getInitialBalance() {
		return initialBalance;
	}

	public static void setInitialBalance(double initialBalance) {
		Broker.initialBalance = initialBalance;
		Broker.balance = new AtomicDouble(initialBalance);
	}

	public static double getBalance() {
		return balance.get();
	}

	public static void setBalance(double newBalance) {
		balance.set(newBalance);
	}

	public static double addBalance(double toAdd) {
		return balance.addAndGet(toAdd);
	}

	public static double subtractBalance(double toSubtract) {
		return balance.addAndGet(-toSubtract);
	}

	public static double updateBalance(Order order) {
		if (order.getSide() == 1) {
			return subtractBalance(order.getRealPricePerShare());
		} else {
			return addBalance(order.getRealPricePerShare());
		}
	}

	public static String getId() {
		return id;
	}
	
	public static void setId(String assignedId) {
		id = assignedId;
	}

	public static Connection getConnection() {
		return connection;
	}

	public static void setConnection(Connection connectionState) {
		connection = connectionState;
	}

	public static void main(String[] args) {
		if (args.length != 1) {
			System.err.println("Usage: Broker <market_id>");
			return;
		}
		var threadPool = Executors.newFixedThreadPool(2);
		var brokerController = new BrokerController();
		var orderService = OrderService.getInstance();
		Broker.setInitialBalance(100000.0);

		threadPool.submit(() -> brokerController.startListening(8000));
		try {
			while (Broker.getConnection() == Connection.UNSET) {
				Thread.sleep(1000);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
			return;
		}
		if (Broker.getConnection() == Connection.ALIVE) {
			threadPool.submit(() -> orderService.startPlacingOrders(args[0], brokerController, new TradingAlgorithmOne()));
		}
		
		threadPool.shutdown();
	}

}
