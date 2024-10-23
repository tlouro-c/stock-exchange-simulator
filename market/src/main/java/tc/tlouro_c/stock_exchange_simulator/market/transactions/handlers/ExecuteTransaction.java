package tc.tlouro_c.stock_exchange_simulator.market.transactions.handlers;

import tc.tlouro_c.stock_exchange_simulator.market.stocks.Stock;
import tc.tlouro_c.stock_exchange_simulator.market.stocks.StockDAO;
import tc.tlouro_c.stock_exchange_simulator.market.stocks.StockDAO.StockVersionConflictException;
import tc.tlouro_c.stock_exchange_simulator.market.transactions.Transaction;
import tc.tlouro_c.stock_exchange_simulator.market.transactions.TransactionType;

public class ExecuteTransaction extends TransactionHandler {

	@Override
	public void handleTransaction(Transaction transaction, Object extra)
			throws TransactionRejectedException, TransactionAcceptedException {
		
		var stockDAO = StockDAO.getInstance();
		var stockData = (Stock) extra;
		boolean buyOrder = transaction.getTransactionType() == TransactionType.BUY;
		var pricePerShare = stockData.getPrice();
		try {
			if (buyOrder) {
				stockData.setShares(stockData.getShares() - transaction.getSharesAmount());
			} else {
				stockData.setShares(stockData.getShares() + transaction.getSharesAmount());
			}
			stockDAO.updateStock(stockData);
			transaction.setPricePerShare(pricePerShare);
	
			throw new TransactionAcceptedException(transaction);
		} catch (StockVersionConflictException e) {
			previousHandler.handleTransaction(transaction, null);
		} catch (TransactionAcceptedException e) {
			throw e;
		} catch (Exception e) {
			throw new TransactionRejectedException(e.getMessage(), transaction);
		}
	}
	
}
