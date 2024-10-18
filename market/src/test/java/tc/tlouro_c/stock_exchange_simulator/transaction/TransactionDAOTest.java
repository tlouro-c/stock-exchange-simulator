package tc.tlouro_c.stock_exchange_simulator.transaction;

import java.time.LocalDateTime;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.Test;

import tc.tlouro_c.stock_exchange_simulator.transactions.Transaction;
import tc.tlouro_c.stock_exchange_simulator.transactions.TransactionDAO;
import tc.tlouro_c.stock_exchange_simulator.transactions.TransactionState;
import tc.tlouro_c.stock_exchange_simulator.transactions.TransactionType;

public class TransactionDAOTest {
	@Test
	void testInsertTransaction() {
		var transactionDAO = TransactionDAO.getInstance();
		var transaction = new Transaction();
		var threadPool = Executors.newFixedThreadPool(10);

		transaction.setStockSymbol("AAPL");
		transaction.setTransactionType(TransactionType.BUY);
		transaction.setTransactionState(TransactionState.EXECUTED);
		transaction.setQuantity(2);
		transaction.setPricePerShare(235.00);
		transaction.setTransactionDate(LocalDateTime.now());

		transactionDAO.initializeTable();
		for (int i = 0; i < 10; i++) {
			threadPool.submit(() -> transactionDAO.insertTransaction(transaction));
		}
		//System.out.println(transactionDAO.insertTransaction(transaction));
	}
}
