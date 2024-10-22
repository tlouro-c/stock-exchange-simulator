package tc.tlouro_c.stock_exchange_simulator.strategies;

import java.nio.channels.Selector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import tc.tlouro_c.stock_exchange_simulator.orders.Order;
import tc.tlouro_c.stock_exchange_simulator.orders.OrderBuilder;
import tc.tlouro_c.stock_exchange_simulator.orders.OrderFactory;

public class TradingAlgorithmOne implements TradingStrategy {

	@Override
	public void start(ConcurrentLinkedQueue<Order> ordersInQueue, Selector selector, String market,
			ConcurrentHashMap<String, Integer> portfolio) {

			for (int i = 0; i < 10; i++) {
				var newOrder = OrderFactory.getInstance().newOrder("BUY");
				newOrder = new OrderBuilder(newOrder).instrument("AAPL")
													.market(market)
													.limitPricePerShare(250)
													.shares(2)
													.build();
				ordersInQueue.add(newOrder);
				selector.wakeup();
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			var portfolioEntrySet = portfolio.entrySet();
			for (var entry : portfolioEntrySet) {
				System.out.println(entry.getKey() + " : " + entry.getValue());
			}
	}
	
}
