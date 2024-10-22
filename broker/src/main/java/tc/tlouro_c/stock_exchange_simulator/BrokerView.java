package tc.tlouro_c.stock_exchange_simulator;

import tc.tlouro_c.stock_exchange_simulator.orders.Order;
import tc.tlouro_c.stock_exchange_simulator.orders.OrderState;

public class BrokerView {

	private String red = "\033[0;31m";
	private String green = "\033[0;32m";
	private String yellow = "\033[0;33m";
	private String blue = "\033[0;34m";
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
}
