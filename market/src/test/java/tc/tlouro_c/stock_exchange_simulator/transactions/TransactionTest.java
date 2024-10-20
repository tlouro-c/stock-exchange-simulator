package tc.tlouro_c.stock_exchange_simulator.transactions;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

public class TransactionTest {
	@Test
	void testToFix() {

		var transactionBuilder = new TransactionBuilder();
		var transaction = transactionBuilder.broker("NASDAQ")
											.market("NYSE")
											.stockSymbol("AAPL")
											.pricePerShare(235)
											.sharesAmount(2)
											.transactionState(TransactionState.EXECUTED)
											.transactionType(TransactionType.BUY)
											.build();
		
		var fixRequest = Transaction.toFix(transaction);

		var buffer = fixRequest.toByteBuffer();
		var bufferStr = StandardCharsets.UTF_8.decode(buffer).toString();
		System.out.println(bufferStr);

		for (var c : bufferStr.toCharArray()) {
			System.out.println("|" + c + "|");
		}

	}
}
