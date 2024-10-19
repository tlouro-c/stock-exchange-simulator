package tc.tlouro_c.stock_exchange_simulator.stocks;

import org.junit.jupiter.api.Test;

public class StockDataProviderTest {
	@Test
	void testGetInstance() {
		var stockProvider = StockInfoProvider.getInstance();

		
		System.out.println(stockProvider.fetchLiveStockPrice("AAPL"));
		System.out.println(stockProvider.fetchLiveStockPrice("GOOG"));
		System.out.println(stockProvider.fetchLiveStockPrice("IBM"));


	}
}
