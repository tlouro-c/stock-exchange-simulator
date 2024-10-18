package tc.tlouro_c.stock_exchange_simulator.transaction;

import org.junit.jupiter.api.Test;

import tc.tlouro_c.stock_exchange_simulator.transactions.TransactionType;

public class TransactionTypeTest {
	@Test
	void testValueOf() {
		var transactionType = TransactionType.SELL;

		System.out.println(transactionType.ordinal());

	}
}
