package tc.tlouro_c.stock_exchange_simulator.router.handlers;

import java.nio.ByteBuffer;
import tc.tlouro_c.stock_exchange_simulator.router.PortThread;

public interface ForwardRequestHandler {

	public void setNextHandler(ForwardRequestHandler nextHandler);
	public void handleRequest(ByteBuffer request, PortThread portThread);
}
