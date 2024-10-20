package tc.tlouro_c.stock_exchange_simulator.stocks;

import org.junit.jupiter.api.Test;

import tc.tlouro_c.stock_exchange_simulator.stocks.StockDAO.StockVersionConflictException;

public class StockDataProviderTest  {
	@Test
	void testGetInstance() throws StockVersionConflictException {
		var stockDAO = StockDAO.getInstance();

		// stockDAO.initializeTable();
		// stockDAO.populateTable();

		var stock = new Stock("AAPL", 10000, 235, 1);

		stockDAO.updateStock(stock);

	}
}
