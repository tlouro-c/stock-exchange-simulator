package tc.tlouro_c.stock_exchange_simulator.transactions;

public class TransactionBuilder {

	private String stockSymbol;
	private int sharesAmount;
	private double pricePerShare;
	private TransactionType transactionType;
	private TransactionState transactionState;
	private String broker;
	private String market;

	public TransactionBuilder stockSymbol(String stockSymbol) {
		this.stockSymbol = stockSymbol;
		return this;
	}
	public TransactionBuilder sharesAmount(int sharesAmount) {
		this.sharesAmount = sharesAmount;
		return this;
	}
	public TransactionBuilder pricePerShare(double pricePerShare) {
		this.pricePerShare = pricePerShare;
		return this;
	}
	public TransactionBuilder transactionType(TransactionType transactionType) {
		this.transactionType = transactionType;
		return this;
	}
	public TransactionBuilder transactionState(TransactionState transactionState) {
		this.transactionState = transactionState;
		return this;
	}
	public TransactionBuilder broker(String broker) {
		this.broker = broker;
		return this;
	}
	public TransactionBuilder market(String market) {
		this.market = market;
		return this;
	}

	public Transaction build() {
		return new Transaction(broker, market, stockSymbol, 
			sharesAmount, pricePerShare, transactionType, transactionState);
	}
	
}