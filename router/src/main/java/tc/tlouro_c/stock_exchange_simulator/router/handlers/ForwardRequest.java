package tc.tlouro_c.stock_exchange_simulator.router.handlers;

import java.nio.channels.SocketChannel;

import tc.tlouro_c.stock_exchange_simulator.FixRequest;
import tc.tlouro_c.stock_exchange_simulator.router.PortsListener;
import tc.tlouro_c.utils.Logger;

public class ForwardRequest extends ForwardRequestHandler {

	@Override
	public void handleRequest(SocketChannel channel, FixRequest request, PortsListener portsListener) {
		var buffer = request.getBuffer();
		buffer.flip();
		portsListener.addToWriteQueue(channel);
		portsListener.addToPendingData(channel, buffer);
		try {
			Logger.SUCCESS("Forwarding message to: " + channel.getRemoteAddress());
		} catch (Exception _e) {}
	}
	
}
