package tc.tlouro_c.stock_exchange_simulator;

import com.google.common.util.concurrent.AtomicDouble;

import tc.tlouro_c.stock_exchange_simulator.orders.Order;
import tc.tlouro_c.stock_exchange_simulator.orders.OrderService;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Broker {

	private static String id = "undefined";
	private static AtomicDouble balance;
	private static double initialBalance;

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

	public static void main(String[] args) {
		ExecutorService threadPool = Executors.newFixedThreadPool(2);
		BrokerController brokerController = new BrokerController();
		OrderService orderService = OrderService.getInstance();
		Broker.setInitialBalance(100000.0);
		threadPool.submit(() -> brokerController.startListening(8000));
		threadPool.submit(() -> orderService.startPlacingOrders("126253", brokerController));
		threadPool.shutdown();
	}

}
