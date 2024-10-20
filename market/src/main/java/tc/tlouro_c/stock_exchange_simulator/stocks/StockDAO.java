package tc.tlouro_c.stock_exchange_simulator.stocks;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import tc.tlouro_c.stock_exchange_simulator.utils.DatabaseConnectionProvider;

public class StockDAO {

	private static StockDAO instance;
	private DatabaseConnectionProvider databaseConnectionProvider;
	private StockRealTimeDataProvider stockRealTimeDataProvider;
	private boolean isTablePopulated;
	
	private StockDAO() {
		databaseConnectionProvider = DatabaseConnectionProvider.getInstance();
		stockRealTimeDataProvider = StockRealTimeDataProvider.getInstance();
		isTablePopulated = false;
	}

	public static StockDAO getInstance() {
		if (instance == null) {
			synchronized(StockDAO.class) {
				if (instance == null) {
					instance = new StockDAO();
				}
			}
		}
		return instance;
	}

	public void initializeTable() {
		
		try (Connection conn = databaseConnectionProvider.getConnection();
			Statement stmt = conn.createStatement()) {
			
			String sql = "CREATE TABLE IF NOT EXISTS stocks ("
					+ "id INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ "symbol TEXT NOT NULL UNIQUE,"
					+ "shares INTEGER NOT NULL,"
					+ "price REAL NOT NULL,"
					+ "version INTEGER NOT NULL DEFAULT 1"
					+ ");";
			stmt.execute(sql);
	
		} catch (SQLException e) {
			System.err.println(e.getMessage());
		}
	}

	public void populateTable() {
		if (isTablePopulated) {
			return;
		}
		String sql = "INSERT INTO stocks(symbol, shares, price) VALUES(?, ?, ?)";
		String[] stocksSymbol = {"AAPL", "AMZN", "TSLA", "MSFT", "JNJ"};
	
		try (Connection conn = databaseConnectionProvider.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql)) {

			for (var symbol : stocksSymbol) {
				pstmt.setString(1, symbol);
				pstmt.setInt(2, 10000);
				pstmt.setDouble(3, stockRealTimeDataProvider.fetchLiveStockPrice(symbol));
				pstmt.execute();
			}
	
		} catch (SQLException e) {
			System.err.println(e.getMessage());
		}
	}

	public Stock fetchStock(String symbol) throws StockNotFoundException {

		Stock stock = null;
		try (Connection conn = databaseConnectionProvider.getConnection();
			Statement stmt = conn.createStatement()) {
			
			String sql = "SELECT symbol, shares, price, version FROM stocks WHERE symbol = '" + symbol + "'"; 
			var rs = stmt.executeQuery(sql);
			if (rs.next()) {
				stock = new Stock(rs.getString("symbol"),
								rs.getInt("shares"),
								rs.getDouble("price"),
								rs.getInt("version"));
			}
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			throw new StockNotFoundException();
		}
		return stock;
	}

	public void updateStock(Stock newStockData) throws StockVersionConflictException {

		String sql = "UPDATE stocks SET shares = ?, price = ?, version = ? WHERE symbol = ? AND version = ?";

		try (Connection conn = databaseConnectionProvider.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql)) {
			
			pstmt.setInt(1, newStockData.getShares());
			pstmt.setDouble(2, newStockData.getPrice());
			pstmt.setInt(3, newStockData.getVersion() + 1);
			pstmt.setString(4, newStockData.getSymbol());
			pstmt.setInt(5, newStockData.getVersion());
			pstmt.executeUpdate();

		} catch (SQLException e) {
			System.err.println(e.getMessage());
			throw new StockVersionConflictException();
		}
	}




	public class StockNotFoundException extends Exception {
		public StockNotFoundException() {
			super("Stock not found");
		}
	}	

	public class StockVersionConflictException extends Exception {
		public StockVersionConflictException() {
			super("Conflict with version");
		}
	}	
}
