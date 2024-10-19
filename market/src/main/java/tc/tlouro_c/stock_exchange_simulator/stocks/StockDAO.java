package tc.tlouro_c.stock_exchange_simulator.stocks;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import tc.tlouro_c.stock_exchange_simulator.utils.DatabaseConnectionProvider;

public class StockDAO {

	private static StockDAO instance;
	private DatabaseConnectionProvider databaseConnectionProvider;
	
	private StockDAO() {
		databaseConnectionProvider = DatabaseConnectionProvider.getInstance();
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
					+ "quantity INTEGER NOT NULL,"
					+ "price REAL NOT NULL,"
					+ ");";
			stmt.execute(sql);
	
		} catch (SQLException e) {
			System.err.println(e.getMessage());
		}
	}

	public Stock fetchStock(String symbol) {

		Stock stock = null;
		try (Connection conn = databaseConnectionProvider.getConnection();
			Statement stmt = conn.createStatement()) {
			
			String sql = "SELECT symbol, quantity, price FROM stocks WHERE symbol = '" + symbol + "'"; 
			var rs = stmt.executeQuery(sql);
			if (rs.first()) {
				stock = new Stock(rs.getString("symbol"),
								rs.getInt("quantity"),
								rs.getDouble("price"));
			}
		} catch (SQLException e) {
			System.err.println(e.getMessage());
		}
		return stock;
	}
	
}
