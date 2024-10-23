package tc.tlouro_c.stock_exchange_simulator.broker;

import tc.tlouro_c.stock_exchange_simulator.broker.orders.Order;
import tc.tlouro_c.stock_exchange_simulator.broker.orders.OrderState;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class BrokerView {

	private String red = "\033[0;31m";
	private String green = "\033[0;32m";
	private String yellow = "\033[0;33m";
	private String reset = "\033[0m";

	public void lostConnectionMessage() {
		System.out.println(red + "Lost connection to the server." + reset);
	}
	
	public void failedConnectionMessage() {
		System.out.println(red + "Failed to connect to the server." + reset);
	}

	public void successfulConnectionMessage() {
		System.out.println(green + "Connected to the server." + reset);
	}

	public void stillConnectingMessage() {
		System.out.println(yellow + "Still connecting..." + reset);
	}

	public void orderOutputMessage(Order order) {
		
		var color = order.getState() == OrderState.EXECUTED ? green : red;

        System.out.println("\n===================================");
        System.out.println("              Order Summary        ");
        System.out.println("===================================");
        System.out.printf("Order ID:    %d%n", order.getId());
        System.out.printf("Status:      %s%s%s%n", color, order.getState(), reset);
		System.out.printf("Instrument:  %s%n", order.getInstrument());
        System.out.printf("Price:       $%.2f%n", order.getRealPricePerShare());
		System.out.printf("Shares:      %d%n", order.getShares());
        System.out.printf("Total:       $%.2f%n", order.getTotal());
        System.out.println("===================================");
    }

	public void brokerBalanceOutput() {
		var initialBalance = Broker.getInitialBalance();
		var balance = Broker.getBalance();
		var difference = balance - initialBalance;
        var profitOrLoss = difference >= 0 ? "Profit" : "Loss";
        
        System.out.println("\n===================================");
        System.out.printf("        Broker Balance Summary     %n");
        System.out.println("===================================");
        System.out.printf("Initial Balance:  $%.2f%n", initialBalance);
        System.out.printf("Updated Balance:  $%.2f%n", balance);
        System.out.printf("Difference:       $%.2f (%s)%n", Math.abs(difference), profitOrLoss);
        System.out.println("===================================");
	}

	public void algorithmFinishedMessage() {
		System.out.println("\nAlgorithm finished.");
	}

	public void goingToManualModeMessage() {
		System.out.println("\nGoing to manual mode.");
	}


	public void placeOrderPrompt() {
		System.out.println("\n-> Place An Order Menu <-");
		System.out.println("[ 1 ]  Buy  |  [ 2 ] Sell  |  [ 3 ] Portfolio  |  [ 4 ] Broker Balance"
					+ "  |  [ 5 ] Exit");
	}

	public void pricePerSharePrompt() {
		System.out.print("Price per stock: ");
	}

	public void instrumentPrompt() {
		System.out.print("Instrument: ");
	}

	public void sharesPrompt() {
		System.out.print("Shares: ");
	}

	public void placingOrderMessage() {
		System.out.println("Placing order...");
	}

	public void printPortfolio(ConcurrentHashMap<String, Integer> portfolio) {

		if (portfolio == null || portfolio.isEmpty()) {
            System.out.println("Your portfolio is currently empty.");
            return;
        }

        System.out.println("========== Your Stock Portfolio ==========");
        System.out.printf("%-10s | %-15s\n", "Symbol", "Shares");
        System.out.println("------------------------------------------");

        for (Map.Entry<String, Integer> entry : portfolio.entrySet()) {
            System.out.printf("%-10s | %-15d\n", entry.getKey(), entry.getValue());
        }

        System.out.println("==========================================");
    }

	public void insufficientFundsMessage() {
		System.out.println(red + "Not enough funds to place the order." + reset);
	}

	public void insufficientSharesMessage() {
		System.out.println(red + "Not enough shares to place the order." + reset);
	}
}
