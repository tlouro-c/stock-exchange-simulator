package tc.tlouro_c.stock_exchange_simulator.router.handlers;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.ByteBuffer;

import org.junit.jupiter.api.Test;


public class ValidateChecksumTest {
	@Test
	public void testValidateChecksum() throws Exception {

		var validator = new ValidateChecksum();

		String[] validRequests = {
			"8=FIX.4.29=7435=A34=97849=TESTSELL352=20190206-16:29:19.20856=TESTBUY398=0108=6010=137",
			"8=FIX.4.29=6235=534=97749=TESTSELL352=20190206-16:28:51.51856=TESTBUY310=092",
			"8=FIX.4.29=7435=A34=97849=TESTSELL352=20190206-16:29:19.20856=TESTBUY398=0108=6010=137",
			"8=FIX.4.29=16335=D34=97249=TESTBUY352=20190206-16:25:10.40356=TESTSELL311=14163685067084226997921=238=10040=154=155=AAPL60=20190206-16:25:08.968207=TO6000=TEST123410=106"
		};
		
		String[] invalidRequests = {
			"8=FIX.4.29=3435=A34=97849=TESTSELL352=20190206-16:29:19.20856=TESTBUY398=0108=6010=137",
			"8=FIX.4.29=6235=534=97749=TESTSELL352=20190206-16:28:51.51856=TESTBUY310=091",
			"8=FIX.4.29=7435=A34=97849=TESTSELL352=20190206-16:19:19.20856=TESTBUY398=0108=6010=137",
			"8=FIX.4.29=16135=D34=97249=TESTBUY352=20190206-16:25:10.40356=TESTSELL311=14163685067084226997921=238=10040=154=155=AAPL60=20190206-16:25:08.968207=TO6000=TEST123410=106"
		};


		for (String validRequest : validRequests) {
			assertDoesNotThrow(() -> 
			validator.validateChecksum(ByteBuffer.wrap(validRequest.getBytes())));
		}

		for (String invalidRequest : invalidRequests) {
			assertThrows(Exception.class, () -> 
			validator.validateChecksum(ByteBuffer.wrap(invalidRequest.getBytes())));
		}

	}
}
