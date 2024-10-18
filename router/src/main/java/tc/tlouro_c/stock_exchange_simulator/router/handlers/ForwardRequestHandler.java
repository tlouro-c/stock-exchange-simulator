package tc.tlouro_c.stock_exchange_simulator.router.handlers;

import java.nio.channels.SocketChannel;

import tc.tlouro_c.stock_exchange_simulator.FixRequest;
import tc.tlouro_c.stock_exchange_simulator.router.PortsListener;

public abstract class ForwardRequestHandler {

	protected ForwardRequestHandler nextHandler;

	public ForwardRequestHandler setNextHandler(ForwardRequestHandler nextHandler) {
		this.nextHandler = nextHandler;
		return this;
	}

	public abstract void handleRequest(SocketChannel channel, FixRequest request,
		PortsListener portsListener) throws Exception;
}
