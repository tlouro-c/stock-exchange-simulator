package tc.tlouro_c.stock_exchange_simulator.market.utils;

import java.sql.Connection;
import java.sql.SQLException;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import tc.tlouro_c.stock_exchange_simulator.market.Market;

public class DatabaseConnectionProvider {

	private static DatabaseConnectionProvider instance;
	private HikariDataSource dataSource;

	private DatabaseConnectionProvider() {
		HikariConfig config = new HikariConfig();
		config.setJdbcUrl("jdbc:sqlite:market[id=" + Market.getId() + "].db");
		this.dataSource = new HikariDataSource(config);
	}

	public static DatabaseConnectionProvider getInstance() {
		if (instance == null) {
			synchronized(DatabaseConnectionProvider.class) {
				if (instance == null) {
					instance = new DatabaseConnectionProvider();
				}
			}
		}
		return instance;
	}

	public Connection getConnection() throws SQLException {
		return dataSource.getConnection();
	}
}
