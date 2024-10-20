package tc.tlouro_c.stock_exchange_simulator.transaction;

import org.junit.jupiter.api.Test;

import tc.tlouro_c.stock_exchange_simulator.transactions.Transaction;
import tc.tlouro_c.stock_exchange_simulator.transactions.TransactionBuilder;


public class TransactionTypeTest {
	@Test
	void testValueOf() {
		var transaction = new TransactionBuilder().build();

		alterStr(transaction);

		System.out.println(transaction);

	}

	void alterStr(Transaction transaction) {
		transaction = new TransactionBuilder().build();
		System.out.println(transaction);
		transaction.setPricePerShare(10);
	}

}
