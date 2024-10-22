package tc.tlouro_c.stock_exchange_simulator.strategies;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentHashMap;

import java.nio.channels.Selector;

import tc.tlouro_c.stock_exchange_simulator.orders.Order;

public interface TradingStrategy {

	public void start(ConcurrentLinkedQueue<Order> ordersInQueue, Selector selector,
						String market, ConcurrentHashMap<String, Integer> portfolio);
}
