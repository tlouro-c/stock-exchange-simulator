package tc.tlouro_c.stock_exchange_simulator.broker.strategies;

import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import tc.tlouro_c.stock_exchange_simulator.broker.Broker;
import tc.tlouro_c.stock_exchange_simulator.broker.BrokerController;
import tc.tlouro_c.stock_exchange_simulator.broker.BrokerView;
import tc.tlouro_c.stock_exchange_simulator.broker.orders.Order;
import tc.tlouro_c.stock_exchange_simulator.broker.orders.OrderBuilder;
import tc.tlouro_c.stock_exchange_simulator.broker.orders.OrderFactory;
import tc.tlouro_c.utils.InputReader;

public class TradingManualMode implements TradingStrategy {

	@Override
	public void start(ConcurrentLinkedQueue<Order> ordersInQueue, BrokerController brokerController,
			String market, ConcurrentHashMap<String, Integer> portfolio) {
		
		var brokerView = brokerController.getBrokerView();
		var ir = InputReader.getInstance();
		
		brokerView.goingToManualModeMessage();

		while (true) {
			var newOrder = processInitialInput(ir, brokerView, portfolio);
			if (newOrder == null) {
				break;
			}

			var orderBuilder = new OrderBuilder(newOrder).market(market);
			brokerView.instrumentPrompt();
			orderBuilder.instrument(ir.getString().toUpperCase());
			brokerView.sharesPrompt();
			orderBuilder.shares(ir.getIntBetween(1, 10000, "Valid range: 1 - 10000"));
			brokerView.pricePerSharePrompt();
			orderBuilder.limitPricePerShare(ir.getDoubleBetween(1, 10000, "Valid range: 1 - 10000"));
			newOrder = orderBuilder.build();
			System.out.println(newOrder);

			if (newOrder.getSide() == 1 && Broker.getBalance() - newOrder.getTotal() < 0) {
				brokerView.insufficientFundsMessage();
				continue;
			} else if (newOrder.getSide() == 2 && (!portfolio.containsKey(newOrder.getInstrument())
					|| portfolio.get(newOrder.getInstrument()) < newOrder.getShares())) {
				brokerView.insufficientSharesMessage();
				continue;
			}
			brokerView.placingOrderMessage();
			try {
				Thread.sleep(500);
				ordersInQueue.add(newOrder);
				brokerController.wakeUpSelector();
				Thread.sleep(1000);
			} catch (Exception _e) {
				_e.printStackTrace();
				break;
			}
		}
		
	}

	private Order processInitialInput(InputReader ir, BrokerView brokerView,
										ConcurrentHashMap<String, Integer> portfolio) {
		while (true) {
			brokerView.placeOrderPrompt();
			var orderSideInput = ir.optionsBasedInput(Arrays.asList(1, 2, 3, 4, 5));
			switch (orderSideInput) {
				case 1:
					return OrderFactory.getInstance().newOrder("BUY");
				case 2:
					return OrderFactory.getInstance().newOrder("SELL");
				case 3:
					brokerView.printPortfolio(portfolio);
					break;
				case 4:
					brokerView.brokerBalanceOutput();
					break;
				default:
					return null;
			}
		}
	}
}


