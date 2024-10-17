package tc.tlouro_c.stock_exchange_simulator.router;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.nio.channels.SocketChannel;

import tc.tlouro_c.utils.Logger;

public class Router {

	private final PortsListener portsListener;
	private final RoutingTable routingTable;
	private List<Integer> idsGenerated;

	private Router() {
		this.portsListener = new PortsListener(this);
		this.routingTable = new RoutingTable();
		this.idsGenerated = new ArrayList<>();
	}

	private void start(List<Integer> ports) {
		Logger.INFO("Router started");
		portsListener.startListening(ports);
	}

	public int getNewId() {
		var newId = (int) ((Math.random() * (999999 - 100000)) + 100000);
		while (idsGenerated.contains(newId)) {
			newId = (int) ((Math.random() * (999999 - 100000)) + 100000);
		}
		idsGenerated.add(newId);
		return newId;
	}
	
	public void addRoute(int id, SocketChannel channel) {
		routingTable.registerRoute(id, channel);
	}
	
	public void removeRoute(SocketChannel channel) {
		routingTable.unregisterRoute(channel);
	}

	public SocketChannel getRoute(int id) {
		return routingTable.get(id);
	}
	
	public String getRoutingTable() {
		return routingTable.toString();
	}

	public static void main(String[] args) {
		Router router = new Router();
		router.start(Arrays.asList(8000, 8001));
	}


}
