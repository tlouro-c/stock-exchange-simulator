package tc.tlouro_c.stock_exchange_simulator;

import tc.tlouro_c.stock_exchange_simulator.transactions.TransactionState;

public class MarketView {

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

	public void processingTransactionMessage() {
		System.out.println(blue + "Processing transaction on thread: " + Thread.currentThread().getName() + reset);
	}

	public void transactionProcessedMessage(TransactionState state) {
		var color = state == TransactionState.EXECUTED ? green : yellow;
		System.out.println(color + "Transaction was processed: " + state.name() + reset);
	}


}
