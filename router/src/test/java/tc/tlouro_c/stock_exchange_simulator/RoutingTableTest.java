package tc.tlouro_c.stock_exchange_simulator;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

import org.junit.jupiter.api.Test;

import tc.tlouro_c.stock_exchange_simulator.router.RoutingTable;

public class RoutingTableTest {
	@Test
	public void testRegisterRoute() throws IOException {

		var routingTable = new RoutingTable();
		var sc = SocketChannel.open();
		sc.bind(new InetSocketAddress("localhost", 8080));

		routingTable.registerRoute(1, sc);
		System.out.println(routingTable);
	}
}
