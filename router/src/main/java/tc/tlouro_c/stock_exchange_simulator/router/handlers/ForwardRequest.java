package tc.tlouro_c.stock_exchange_simulator.router.handlers;

import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

import tc.tlouro_c.stock_exchange_simulator.FixRequest;
import tc.tlouro_c.stock_exchange_simulator.router.RouterController;
import tc.tlouro_c.utils.Logger;

public class ForwardRequest extends ForwardRequestHandler {

	@Override
	public void handleRequest(SocketChannel channel, FixRequest request, RouterController routerController) {
		var buffer = request.getBuffer();
		buffer.flip();
		routerController.addToWriteQueue(channel);
		routerController.addToPendingData(channel, buffer);
		try {
			String messageContent = StandardCharsets.UTF_8.decode(buffer.duplicate()).toString();
			Logger.SUCCESS("Forwarding message to: " + channel.getRemoteAddress() +
					System.lineSeparator() + "Message content: " + messageContent);
		} catch (Exception ignored) {}
	}
	
}
