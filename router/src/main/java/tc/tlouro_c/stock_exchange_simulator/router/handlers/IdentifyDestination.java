package tc.tlouro_c.stock_exchange_simulator.router.handlers;

import java.nio.channels.SocketChannel;

import tc.tlouro_c.stock_exchange_simulator.FixRequest;
import tc.tlouro_c.stock_exchange_simulator.router.PortsListener;
import tc.tlouro_c.utils.Logger;

public class IdentifyDestination extends ForwardRequestHandler {

	@Override
	public void handleRequest(SocketChannel channel, FixRequest request,
		PortsListener portsListener) throws InvalidDestinationException {
		
		try {
			var targetChannel = portsListener.fetchFromRoutingTable(
									Integer.parseInt(request.getTargetId()));
			if (targetChannel == null) {
				throw new InvalidDestinationException();
			}
			nextHandler.handleRequest(targetChannel, request, portsListener);
		} catch (Exception e) {
			try {
				Logger.WARNING("Invalid destination for message from " + channel.getRemoteAddress());
			} catch (Exception _e) {}
			throw new InvalidDestinationException();
		}
	}

	private class InvalidDestinationException extends Exception {

		private InvalidDestinationException() {
			super("Invalid destination");
		}
	}
	
}
