package tc.tlouro_c.stock_exchange_simulator.transaction;

import java.nio.ByteBuffer;

import org.junit.jupiter.api.Test;

public class TransactionTypeTest {
	@Test
	void testValueOf() {

		var tmpBuffer = ByteBuffer.wrap("Hello World".getBytes());

		System.out.println(new String(tmpBuffer.array()));
		
	}

}
