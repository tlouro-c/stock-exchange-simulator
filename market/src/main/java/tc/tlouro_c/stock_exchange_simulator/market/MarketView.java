package tc.tlouro_c.stock_exchange_simulator.market;

import tc.tlouro_c.stock_exchange_simulator.market.transactions.TransactionState;
import tc.tlouro_c.utils.Logger;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

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

	public void processingTransactionMessage(ByteBuffer buffer) {
		String messageContent = StandardCharsets.UTF_8.decode(buffer.duplicate()).toString();
		Logger.INFO("Processing transaction..." +
				System.lineSeparator() + "Message content: " + messageContent);
	}

	public void transactionProcessedMessage(TransactionState state) {
		var color = state == TransactionState.EXECUTED ? green : yellow;
		System.out.println(color + "Transaction was processed: " + state.name() + reset);
	}

	public void marketAdjustmentOutput(String stockSymbol, double oldPrice, double newPrice) {

		var marketAdjustment = newPrice - oldPrice;
		var percentageChange = (marketAdjustment / newPrice) * 100;

        if (marketAdjustment > 0) {
            System.out.println(stockSymbol + " Market Adjustment: " + newPrice
				 + green + " ( " + String.format("%.2f", percentageChange) + "% )" + reset);
        } else {
            System.out.println(stockSymbol + " Market Adjustment: " + newPrice
				 + red + " ( " + String.format("%.2f", percentageChange) + "% )" + reset);
        }
    }


}
