package tc.tlouro_c.stock_exchange_simulator.broker.orders;

import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import tc.tlouro_c.stock_exchange_simulator.broker.Broker;
import tc.tlouro_c.stock_exchange_simulator.broker.BrokerController;
import tc.tlouro_c.stock_exchange_simulator.broker.BrokerView;
import tc.tlouro_c.stock_exchange_simulator.Connection;
import tc.tlouro_c.stock_exchange_simulator.FixRequest;
import tc.tlouro_c.stock_exchange_simulator.broker.strategies.TradingStrategy;
import tc.tlouro_c.stock_exchange_simulator.broker.strategies.TradingManualMode;

import java.util.concurrent.ExecutorService;

public class OrderService {

	private static OrderService instance;
	private ConcurrentHashMap<Integer, Order> pendingOrders;
	private ConcurrentLinkedQueue<Order> ordersInQueue;
	private ConcurrentHashMap<String, Integer> portfolio;

	private OrderService() {
		pendingOrders = new ConcurrentHashMap<>();
		ordersInQueue = new ConcurrentLinkedQueue<>();
		portfolio = new ConcurrentHashMap<>();
	};

	public static OrderService getInstance() {
		if (instance == null) {
			synchronized (OrderService.class) {
				if (instance == null) {
					instance = new OrderService();
				}
			}
		}
		return instance;
	}

	public void placeOrder(Order order) {
		ordersInQueue.add(order);
	}

	public Order retrieveOrder(ByteBuffer buffer) throws Exception {
		var fixRequest = new FixRequest(buffer);
		fixRequest.parse();
		var clientOrderId = fixRequest.getClientOrderId();
		var orderState = OrderState.fromValue(Integer.parseInt(fixRequest.getState()));

		var order = pendingOrders.get(clientOrderId);
		if (order == null) {
			throw new OrderNotFoundException();
		}
		pendingOrders.remove(clientOrderId);
		order.setRealPricePerShare(fixRequest.getPricePerShare());
		order.setState(orderState);
		return order;
	}

	public void processOrderResult(ByteBuffer buffer, BrokerView brokerView) {

		try {
			var order = retrieveOrder(buffer);
			
			if (order.getState() == OrderState.EXECUTED) {
				Broker.updateBalance(order);
				var instrument = order.getInstrument();
				if (order.getSide() == 1) {
					portfolio.compute(instrument, (k, v) -> v == null ? order.getShares() : v + order.getShares());
				} else {
					portfolio.compute(instrument, (k, v) -> v == null ? null : v - order.getShares());
				}
			}
			brokerView.orderOutputMessage(order);
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}

	public void startPlacingOrders(String market, BrokerController brokerController, TradingStrategy tradingStrategy, ExecutorService threadPool) {
		try {
			while (Broker.getConnection() == Connection.UNSET) {
				Thread.sleep(1000);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if (Broker.getConnection() == Connection.ALIVE) {
			tradingStrategy.start(ordersInQueue, brokerController, market, portfolio);
		}
		if (Broker.getConnection() == Connection.ALIVE && !(tradingStrategy instanceof TradingManualMode)) {
			brokerController.getBrokerView().algorithmFinishedMessage();
			tradingStrategy = new TradingManualMode();
			tradingStrategy.start(ordersInQueue, brokerController, market, portfolio);
		}
		System.exit(0);
	}

	

	public Order getNextOrder() {
		return ordersInQueue.poll();
	}

	public boolean hasOrdersInQueue() {
		return !ordersInQueue.isEmpty();
	}

	public void addToPendingOrders(Order order) {
		pendingOrders.put(order.getId(), order);
	}

	private class OrderNotFoundException extends Exception {
		private OrderNotFoundException() {
			super("Order not found");
		}
	}
}
