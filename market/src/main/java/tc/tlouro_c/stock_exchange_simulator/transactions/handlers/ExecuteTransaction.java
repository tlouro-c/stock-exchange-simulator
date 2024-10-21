package tc.tlouro_c.stock_exchange_simulator.transactions.handlers;

import tc.tlouro_c.stock_exchange_simulator.stocks.Stock;
import tc.tlouro_c.stock_exchange_simulator.stocks.StockDAO;
import tc.tlouro_c.stock_exchange_simulator.stocks.StockDAO.StockVersionConflictException;
import tc.tlouro_c.stock_exchange_simulator.transactions.Transaction;
import tc.tlouro_c.stock_exchange_simulator.transactions.TransactionType;

public class ExecuteTransaction extends TransactionHandler {

	TransactionHandler previousHandler;

	@Override
	public void handleTransaction(Transaction transaction, Object extra)
			throws TransactionRejectedException, TransactionAcceptedException {

		try {
			var stockDAO = StockDAO.getInstance();
			var stockData = (Stock) extra;
			boolean buyOrder = transaction.getTransactionType() == TransactionType.BUY;
			var pricePerShare = stockData.getPrice();
			if (buyOrder) {
				stockData.setShares(stockData.getShares() - transaction.getSharesAmount());
				stockData.setPrice(Math.round((pricePerShare + 0.005 * transaction.getSharesAmount()) * 1000.0) / 1000.0); // Simulate market activity
			} else {
				stockData.setShares(stockData.getShares() + transaction.getSharesAmount());
				stockData.setPrice(Math.round((pricePerShare - 0.005 * transaction.getSharesAmount()) * 1000.0) / 1000.0); // Simulate market activity
			}
			stockDAO.updateStock(stockData);
			transaction.setPricePerShare(pricePerShare);
	
			throw new TransactionAcceptedException(transaction);
		} catch (TransactionAcceptedException e) {
			throw e;
		} catch (StockVersionConflictException e) {
			System.err.println(e.getMessage());
			previousHandler.handleTransaction(transaction, null);
		} catch (Exception e) {
			throw new TransactionRejectedException(e.getMessage(), transaction);
		}
	}
	
}
