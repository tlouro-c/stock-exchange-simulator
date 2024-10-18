package tc.tlouro_c.stock_exchange_simulator.transactions;

import java.time.LocalDateTime;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

public class Transaction {

	@NotBlank
	private String stockSymbol;

	@Positive
	private int quantity;

	@PositiveOrZero
	private double pricePerShare;

	@PastOrPresent
	private LocalDateTime transactionDate;

	private double total;
	private TransactionType transactionType;
	private TransactionState transactionState;

	public boolean isProcessed() {
		return transactionState != TransactionState.NEW;
	}

	public String getStockSymbol() {
		return stockSymbol;
	}

	public void setStockSymbol(String stockSymbol) {
		this.stockSymbol = stockSymbol;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
		total = quantity * pricePerShare;
	}

	public double getPricePerShare() {
		return pricePerShare;
	}

	public void setPricePerShare(double pricePerShare) {
		this.pricePerShare = pricePerShare;
		total = quantity * pricePerShare;
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
	
}
