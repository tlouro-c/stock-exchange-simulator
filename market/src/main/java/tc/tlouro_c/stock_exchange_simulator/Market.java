package tc.tlouro_c.stock_exchange_simulator;

import java.sql.SQLException;

import tc.tlouro_c.stock_exchange_simulator.utils.DatabaseConnectionProvider;

public class Market {

	private static String id = "undefined";

	public static void setId(String assignedId) {
		id = assignedId;
	}

	public static String getId() {
		return id;
	}

    public static void main(String[] args) {
        var databaseConnectionProvider = DatabaseConnectionProvider.getInstance();
		try {
			databaseConnectionProvider.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }
}
