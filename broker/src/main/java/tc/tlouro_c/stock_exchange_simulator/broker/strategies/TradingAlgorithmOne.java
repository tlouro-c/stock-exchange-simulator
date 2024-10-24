package tc.tlouro_c.stock_exchange_simulator.broker.strategies;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import tc.tlouro_c.stock_exchange_simulator.broker.Broker;
import tc.tlouro_c.stock_exchange_simulator.broker.BrokerController;
import tc.tlouro_c.stock_exchange_simulator.broker.orders.Order;
import tc.tlouro_c.stock_exchange_simulator.broker.orders.OrderBuilder;
import tc.tlouro_c.stock_exchange_simulator.broker.orders.OrderFactory;

public class TradingAlgorithmOne implements TradingStrategy {

	@Override
	public void start(ConcurrentLinkedQueue<Order> ordersInQueue, BrokerController brokerController, 
			String market, ConcurrentHashMap<String, Integer> portfolio) {
			
			var brokerView = brokerController.getBrokerView();
			
			var limitBuyPrice = 240;
			var limitSellPrice = 245;
			while (Broker.getBalance() > 0) {
				brokerView.brokerBalanceOutput();
				brokerView.printPortfolio(portfolio);
				var newBuyOrder = OrderFactory.getInstance().newOrder("BUY");
				newBuyOrder = new OrderBuilder(newBuyOrder).instrument("AAPL")
															.market(market)
															.limitPricePerShare(limitBuyPrice)
															.shares(10)
															.build();
				var newSellOrder = OrderFactory.getInstance().newOrder("SELL");
				newSellOrder = new OrderBuilder(newSellOrder).instrument("AAPL")
															.market(market)
															.limitPricePerShare(limitSellPrice)
															.shares(10)
															.build();
				if (Broker.getBalance() - newBuyOrder.getTotal() >= 0) {
					ordersInQueue.add(newBuyOrder);
					brokerView.placingBuyOrderMessage();
				} else {
					brokerView.insufficientFundsMessage();
				}
				if (portfolio.containsKey(newBuyOrder.getInstrument()) 
					&& portfolio.get(newSellOrder.getInstrument()) >= newSellOrder.getShares()) {
					ordersInQueue.add(newSellOrder);
					brokerView.placingSellOrderMessage();
				} else {
					brokerView.insufficientSharesMessage();
				}

				brokerController.wakeUpSelector();
				try {
					Thread.sleep(5000);
				} catch (Exception e) {
					continue;
				}
			}
	}
	
}
