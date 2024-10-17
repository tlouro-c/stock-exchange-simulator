package tc.tlouro_c.stock_exchange_simulator.router.handlers;

import java.nio.channels.SocketChannel;

import tc.tlouro_c.stock_exchange_simulator.FixRequest;
import tc.tlouro_c.stock_exchange_simulator.router.PortsListener;
import tc.tlouro_c.utils.Logger;

public class ValidateChecksum extends ForwardRequestHandler {

	@Override
	public void handleRequest(SocketChannel channel, FixRequest request,
		PortsListener portsListener) throws FixRequest.InvalidChecksumException {

		try {
			request.parse();
			request.validateChecksum();
			nextHandler.handleRequest(channel, request, portsListener);
		} catch (Exception e) {
			try {
				Logger.WARNING("Checksum validation failed for message from " + channel.getRemoteAddress());
			} catch (Exception _e) {}
			throw new FixRequest.InvalidChecksumException();
		}
	}
}
