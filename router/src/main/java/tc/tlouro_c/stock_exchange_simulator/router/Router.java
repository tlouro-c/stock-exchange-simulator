package tc.tlouro_c.stock_exchange_simulator.router;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Router {

	private static RoutingTable routingTable;
	private static int idCounter = 100000;

    public static void main(String[] args) {
        System.out.println("Starting the Router");
		routingTable = new RoutingTable();

		var portHandlerOne = new PortThread(routingTable, 8000);
		var portHandlerTwo = new PortThread(routingTable, 8001);
		ExecutorService threadPool = Executors.newFixedThreadPool(2);
		threadPool.submit(portHandlerOne);
		threadPool.submit(portHandlerTwo);
    }

	public static int getAndIncrementId() {
		return idCounter++;
	}
}
