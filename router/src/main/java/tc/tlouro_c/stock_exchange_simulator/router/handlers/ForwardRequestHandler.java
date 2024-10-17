package tc.tlouro_c.stock_exchange_simulator.router.handlers;

import java.nio.channels.SocketChannel;

import tc.tlouro_c.stock_exchange_simulator.FixRequest;
import tc.tlouro_c.stock_exchange_simulator.router.PortsListener;

public abstract class ForwardRequestHandler {

	protected ForwardRequestHandler nextHandler;

	public void setNextHandler(ForwardRequestHandler nextHandler) {
		this.nextHandler = nextHandler;
	}

	public abstract void handleRequest(SocketChannel channel, FixRequest request,
		PortsListener portsListener) throws Exception;
}
