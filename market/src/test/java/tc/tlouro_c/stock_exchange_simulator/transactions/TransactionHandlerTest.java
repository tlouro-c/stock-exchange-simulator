package tc.tlouro_c.stock_exchange_simulator.transactions;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

public class TransactionHandlerTest {
	@Test
	void testBuildChain() {

		List<Integer> list = Arrays.asList(1, 2, 4);

		var listIt = list.iterator();
		while (listIt.hasNext()) {
			System.out.println(listIt.next());
		}

		System.out.println(list.iterator().next());

	}
}
