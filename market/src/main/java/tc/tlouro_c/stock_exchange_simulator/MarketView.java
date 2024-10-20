package tc.tlouro_c.stock_exchange_simulator;

public class MarketView {

	public void lostConnectionMessage() {
		System.out.println("Connection lost... Please check the server.");
	}
	
	public void failedConnectionMessage() {
		System.out.println("Connection failed. Please check the server.");
	}

	public void successfulConnectionMessage() {
		System.out.println("Connection established!");
	}

	public void stillConnectingMessage() {
		System.out.println("Connecting...");
	}
}
