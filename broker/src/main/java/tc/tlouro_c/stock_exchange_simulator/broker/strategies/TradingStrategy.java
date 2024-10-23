package tc.tlouro_c.stock_exchange_simulator.broker.strategies;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentHashMap;

import tc.tlouro_c.stock_exchange_simulator.broker.BrokerController;
import tc.tlouro_c.stock_exchange_simulator.broker.orders.Order;

public interface TradingStrategy {

	public void start(ConcurrentLinkedQueue<Order> ordersInQueue, BrokerController brokerController,
						String market, ConcurrentHashMap<String, Integer> portfolio);
}
