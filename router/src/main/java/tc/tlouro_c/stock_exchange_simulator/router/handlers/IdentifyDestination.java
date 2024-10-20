package tc.tlouro_c.stock_exchange_simulator.router.handlers;

import java.nio.channels.SocketChannel;

import tc.tlouro_c.stock_exchange_simulator.FixRequest;
import tc.tlouro_c.stock_exchange_simulator.router.RouterController;
import tc.tlouro_c.utils.Logger;

public class IdentifyDestination extends ForwardRequestHandler {

	@Override
	public void handleRequest(SocketChannel channel, FixRequest request,
		RouterController routerController) throws InvalidDestinationException {
		
		try {
			var targetChannel = routerController.fetchFromRoutingTable(
									Integer.parseInt(request.getTargetId()));
			if (targetChannel == null) {
				throw new InvalidDestinationException();
			}
			nextHandler.handleRequest(targetChannel, request, routerController);
		} catch (Exception e) {
			try {
				Logger.WARNING("Invalid destination for message from " + channel.getRemoteAddress());
			} catch (Exception _e) {}
			throw new InvalidDestinationException();
		}
	}

	public class InvalidDestinationException extends Exception {

		public InvalidDestinationException() {
			super("Invalid destination");
		}
	}
	
}
