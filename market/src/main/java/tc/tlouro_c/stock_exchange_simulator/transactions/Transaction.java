package tc.tlouro_c.stock_exchange_simulator.transactions;

import java.time.LocalDateTime;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import tc.tlouro_c.stock_exchange_simulator.FixRequest;
import tc.tlouro_c.stock_exchange_simulator.Market;

public class Transaction {

	@NotBlank
	private String stockSymbol;

	@Positive
	private int sharesAmount;

	@PositiveOrZero
	private double pricePerShare;

	@NotBlank
	private String broker;

	@NotBlank
	private String market;

	@NotNull
	private TransactionType transactionType;

	private double total;
	private LocalDateTime transactionDate;
	private TransactionState transactionState;
	private int clientOrderId;
	
	Transaction(String broker, String market, int clientOrderId,
				String stockSymbol, int sharesAmount, double pricePerShare, 
				TransactionType transactionType, TransactionState transactionState) {
		this.broker = broker;
		this.market = market;
		this.clientOrderId = clientOrderId;
		this.stockSymbol = stockSymbol;
		this.sharesAmount = sharesAmount;
		this.pricePerShare = pricePerShare;
		this.transactionType = transactionType;
		this.transactionState = transactionState;
		this.transactionDate = LocalDateTime.now();
	}

	public static Transaction fromFix(FixRequest fixRequest) {
		var transactionBuilder = new TransactionBuilder();

		transactionBuilder.market(Market.getId())
						.broker(fixRequest.getSenderId())
						.clientOrderId(fixRequest.getClientOrderId())
						.stockSymbol(fixRequest.getInstrument())
						.sharesAmount(fixRequest.getSharesAmount())
						.pricePerShare(fixRequest.getPricePerShare())
						.transactionType(TransactionType.valueOf(fixRequest.getOrderType()))
						.transactionState(TransactionState.NEW);
		
		return transactionBuilder.build();
	}

	public static FixRequest toFix(Transaction transaction) {

		var fixRequest = new FixRequest();

		fixRequest.setSenderId(transaction.market);
		fixRequest.setMarketId(transaction.market);
		fixRequest.setClientOrderId(transaction.clientOrderId);
		fixRequest.setTargetId(transaction.broker);
		fixRequest.setInstrument(transaction.stockSymbol);
		fixRequest.setOrderType(String.valueOf(transaction.transactionType.getValue()));
		fixRequest.setPricePerShare(transaction.pricePerShare);
		fixRequest.setSharesAmount(transaction.sharesAmount);
		fixRequest.setState(String.valueOf(transaction.transactionState.getValue()));

		return fixRequest;
	}

	public boolean isProcessed() {
		return transactionState != TransactionState.NEW;
	}

	public String getStockSymbol() {
		return stockSymbol;
	}

	public void setStockSymbol(String stockSymbol) {
		this.stockSymbol = stockSymbol;
	}

	public int getSharesAmount() {
		return sharesAmount;
	}

	public void setSharesAmount(int sharesAmount) {
		this.sharesAmount = sharesAmount;
		total = sharesAmount * pricePerShare;
	}

	public double getPricePerShare() {
		return pricePerShare;
	}

	public void setPricePerShare(double pricePerShare) {
		this.pricePerShare = pricePerShare;
		total = sharesAmount * pricePerShare;
	}

	public LocalDateTime getTransactionDate() {
		return transactionDate;
	}

	public double getTotal() {
		return total;
	}

	public void setTotal(double total) {
		this.total = total;
	}

	public void setTransactionDate(LocalDateTime transactionDate) {
		this.transactionDate = transactionDate;
	}

	public TransactionType getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(TransactionType transactionType) {
		this.transactionType = transactionType;
	}

	public TransactionState getTransactionState() {
		return transactionState;
	}

	public void setTransactionState(TransactionState transactionState) {
		this.transactionState = transactionState;
	}

	public String getBroker() {
		return broker;
	}

	public void setBroker(String broker) {
		this.broker = broker;
	}

	public String getMarket() {
		return market;
	}

	public void setMarket(String market) {
		this.market = market;
	}

	public int getClientOrderId() {
		return clientOrderId;
	}

	public void setClientOrderId(int clientOrderId) {
		this.clientOrderId = clientOrderId;
	}

}
