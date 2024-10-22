package tc.tlouro_c.stock_exchange_simulator.stocks;

public class Stock {

	private String symbol;
	private double price;
	private int shares;
	private int version;

	public Stock(String symbol, int availableShares, double price, int version) {
		this.symbol = symbol;
		this.shares = availableShares;
		this.price = price;
		this.version = version;
	}

	public Stock copy() {
		var copy = new Stock(symbol, shares, price, version);
		return copy;
	}

	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public int getShares() {
		return shares;
	}
	public void setShares(int shares) {
		this.shares = shares;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
	}
	
	@Override
	public String toString() {
		return "Stock [symbol=" + symbol + ", price=" + price + ", shares=" + shares + ", version=" + version + "]";
	}

}
