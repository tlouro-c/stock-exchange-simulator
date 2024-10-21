package tc.tlouro_c.stock_exchange_simulator.transactions.handlers;

import tc.tlouro_c.stock_exchange_simulator.stocks.StockDAO;
import tc.tlouro_c.stock_exchange_simulator.transactions.Transaction;
import tc.tlouro_c.stock_exchange_simulator.transactions.TransactionType;

public class CheckStockAvailability extends TransactionHandler {

	@Override
	public void handleTransaction(Transaction transaction, Object extra)
			throws TransactionRejectedException, TransactionAcceptedException {

		try {
			var stockDAO = StockDAO.getInstance();
			var stockData = stockDAO.fetchStock(transaction.getStockSymbol());

			if (transaction.getTransactionType() == TransactionType.BUY 
				&& (transaction.getSharesAmount() > stockData.getShares()
				|| transaction.getPricePerShare() < stockData.getPrice())) {
					throw new TransactionRejectedException(transaction);
			} else if (transaction.getTransactionType() == TransactionType.SELL 
				&& transaction.getPricePerShare() > stockData.getPrice()) {
					throw new TransactionRejectedException(transaction);
			}
			nextHandler.setPreviousHandler(this);
			nextHandler.handleTransaction(transaction, stockData);
		} catch (TransactionRejectedException e) {
			throw e;
		} catch (TransactionAcceptedException e) {
			throw e;
		} catch (Exception e) {
			throw new TransactionRejectedException(e.getMessage(), transaction);
		}
	}
	
}
