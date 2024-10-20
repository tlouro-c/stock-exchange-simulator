package tc.tlouro_c.stock_exchange_simulator.transactions.handlers;

import tc.tlouro_c.stock_exchange_simulator.stocks.Stock;
import tc.tlouro_c.stock_exchange_simulator.stocks.StockDAO;
import tc.tlouro_c.stock_exchange_simulator.stocks.StockDAO.StockVersionConflictException;
import tc.tlouro_c.stock_exchange_simulator.transactions.Transaction;

public class ExecuteTransaction extends TransactionHandler {

	TransactionHandler previousHandler;

	@Override
	public void handleTransaction(Transaction transaction, Object extra)
			throws TransactionRejectedException, TransactionAcceptedException {

		try {
			var stockDAO = StockDAO.getInstance();
			var stockData = (Stock) extra;
			var pricePerShare = stockData.getPrice();
			stockData.setShares(stockData.getShares() - transaction.getSharesAmount());
			stockData.setPrice(pricePerShare + 0.005);
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
