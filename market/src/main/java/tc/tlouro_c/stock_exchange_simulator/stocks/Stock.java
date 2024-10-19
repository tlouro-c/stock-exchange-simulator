package tc.tlouro_c.stock_exchange_simulator.stocks;

public class Stock {

	private String symbol;
	private double currentPrice;
	private int availableQuantity;

	public Stock(String symbol, int availableQuantity, double currentPrice) {
		this.symbol = symbol;
		this.availableQuantity = availableQuantity;
		this.currentPrice = currentPrice;
	}

	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public int getAvailableQuantity() {
		return availableQuantity;
	}
	public void setAvailableQuantity(int availableQuantity) {
		this.availableQuantity = availableQuantity;
	}
	public double getCurrentPrice() {
		return currentPrice;
	}
	public void setCurrentPrice(double currentPrice) {
		this.currentPrice = currentPrice;
	}
	
	@Override
	public String toString() {
		return "Stock [symbol=" + symbol + ", availableQuantity=" + availableQuantity + ", currentPrice=" + currentPrice + "]";
	}

}
