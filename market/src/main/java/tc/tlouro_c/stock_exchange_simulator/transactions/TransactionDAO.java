package tc.tlouro_c.stock_exchange_simulator.transactions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.format.DateTimeFormatter;

import tc.tlouro_c.stock_exchange_simulator.utils.DatabaseConnectionProvider;

public class TransactionDAO {

	private static TransactionDAO instance;
	private DatabaseConnectionProvider databaseConnectionProvider;
	
	private TransactionDAO() {
		databaseConnectionProvider = DatabaseConnectionProvider.getInstance();
	}

	public static TransactionDAO getInstance() {
		if (instance == null) {
			synchronized(TransactionDAO.class) {
				if (instance == null) {
					instance = new TransactionDAO();
				}
			}
		}
		return instance;
	}

	public void initializeTable() {

		try (Connection conn = databaseConnectionProvider.getConnection();
			Statement stmt = conn.createStatement()) {

			String sql = "CREATE TABLE IF NOT EXISTS transactions ("
					+ "id INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ "type TEXT NOT NULL,"
					+ "state TEXT NOT NULL,"
					+ "symbol TEXT NOT NULL,"
					+ "quantity INTEGER NOT NULL,"
					+ "price_per_share REAL NOT NULL,"
					+ "total REAL NOT NULL,"
					+ "date TEXT NOT NULL"
					+ ");";
			stmt.execute(sql);
	
		} catch (SQLException e) {
			System.err.println(e.getMessage());
		}
	}

	public long insertTransaction(Transaction transaction) {
	
		if (!transaction.isProcessed()) {
			System.out.print("This transaction wasn't processed yet.");
			return -1;
		}

		long id = -1;
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		String sql = "INSERT INTO transactions"
					+ "(type, state, symbol, quantity, price_per_share, total, date)"
					+ "VALUES (?, ?, ?, ?, ?, ?, ?)";

		try (Connection conn = databaseConnectionProvider.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql)) {
			
			pstmt.setString(1, transaction.getTransactionType().name());
			pstmt.setString(2, transaction.getTransactionState().name());
			pstmt.setString(3, transaction.getStockSymbol());
			pstmt.setInt(4, transaction.getQuantity());
			pstmt.setDouble(5, transaction.getPricePerShare());
			pstmt.setDouble(6, transaction.getTotal());
			pstmt.setString(7, transaction.getTransactionDate().format(dateFormatter));
			pstmt.executeUpdate();
			id = pstmt.getGeneratedKeys().getLong(1);

		} catch (SQLException e) {
			System.err.println(e.getMessage());
		}
		return id;
	}
}
