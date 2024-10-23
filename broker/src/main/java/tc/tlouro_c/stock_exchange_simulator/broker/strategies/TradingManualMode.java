package tc.tlouro_c.stock_exchange_simulator.broker.strategies;

import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import tc.tlouro_c.stock_exchange_simulator.broker.BrokerController;
import tc.tlouro_c.stock_exchange_simulator.broker.orders.Order;
import tc.tlouro_c.stock_exchange_simulator.broker.orders.OrderBuilder;
import tc.tlouro_c.stock_exchange_simulator.broker.orders.OrderFactory;
import tc.tlouro_c.utils.InputReader;

public class TradingManualMode implements TradingStrategy {

	@Override
	public void start(ConcurrentLinkedQueue<Order> ordersInQueue, BrokerController brokerController,
			String market, ConcurrentHashMap<String, Integer> portfolio) {
		
		var brokerView = brokerController.getBrokerView();
		var inputReader = InputReader.getInstance();
		
		brokerView.goingToManualModeMessage();

		while (true) {
			brokerView.placeOrderPrompt();
			var input = inputReader.optionsBasedInput(Arrays.asList(1, 2, 3));
			Order newOrder;
			if (input == 1) {
				newOrder = OrderFactory.getInstance().newOrder("BUY");
			} else if (input == 2) {
				newOrder = OrderFactory.getInstance().newOrder("SELL");
			} else {
				break;
			}

			// var orderBuilder = new OrderBuilder(newOrder);
			// brokerView.instrumentPrompt();
			// input = inputReader.optionsBasedInputString(Arrays.asList("AAPL", ""))

		}
		
	}

	
	
}
