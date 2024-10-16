package tc.tlouro_c.stock_exchange_simulator.router.handlers;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import tc.tlouro_c.stock_exchange_simulator.router.PortThread;

public class ValidateChecksum implements ForwardRequestHandler {

	ForwardRequestHandler nextHandler;

	@Override
	public void setNextHandler(ForwardRequestHandler nextHandler) {
		this.nextHandler = nextHandler;
	}

	@Override
	public void handleRequest(ByteBuffer request, PortThread portThread) {

		try {
			validateChecksum(request);
			nextHandler.handleRequest(request, portThread);
		} catch (InvalidChecksumException e) {
			//TODO Handle invalid checksum
		}
	}

	public void validateChecksum(ByteBuffer request) throws InvalidChecksumException {

		String requestString = StandardCharsets.UTF_8.decode(request).toString();
		try {
			int checksumIndex = requestString.lastIndexOf("10=");
			String checksum = requestString.substring(checksumIndex + 3, requestString.length() - 1);
			String toSum = requestString.substring(0, checksumIndex);

			if (Integer.parseInt(checksum) != toSum.chars().sum() % 256) {
				throw new InvalidChecksumException();
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new InvalidChecksumException("Invalid request");
		}
	}

	private class InvalidChecksumException extends Exception {

		private InvalidChecksumException(String message) {
			super(message);
		}
		
		private InvalidChecksumException() {
			super("Invalid checksum");
		}
	}

}
