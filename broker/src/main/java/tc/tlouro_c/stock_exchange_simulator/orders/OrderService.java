package tc.tlouro_c.stock_exchange_simulator.orders;

import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import tc.tlouro_c.stock_exchange_simulator.Broker;
import tc.tlouro_c.stock_exchange_simulator.BrokerController;
import tc.tlouro_c.stock_exchange_simulator.BrokerView;
import tc.tlouro_c.stock_exchange_simulator.FixRequest;


import java.util.Scanner;

public class OrderService {

	private static OrderService instance;
	private ConcurrentHashMap<Integer, Order> pendingOrders;
	private ConcurrentLinkedQueue<Order> ordersInQueue;

	private OrderService() {
		pendingOrders = new ConcurrentHashMap<>();
		ordersInQueue = new ConcurrentLinkedQueue<>();
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
			}
			brokerView.orderOutputMessage(order);
			brokerView.brokerBalanceOutput();
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}

	public void startPlacingOrders(String market, BrokerController brokerController) {

		while (brokerController.isConnected()) {
			try {
				Thread.sleep(4000);
				var orderFactory = OrderFactory.getInstance();
				var newBuyOrder = orderFactory.newOrder("BUY");
				var orderBuilder = new OrderBuilder(newBuyOrder);
				newBuyOrder = orderBuilder.instrument("AAPL")
										.limitPricePerShare(250)
										.shares(1)
										.market(market)
										.build();
				placeOrder(newBuyOrder);
				if (brokerController.isConnected()) {
					brokerController.wakeUpSelector();
				}
			} catch(Exception e) {
				break;
			}
		}
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
