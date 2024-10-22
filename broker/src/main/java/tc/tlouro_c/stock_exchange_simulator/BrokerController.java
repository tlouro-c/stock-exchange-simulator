package tc.tlouro_c.stock_exchange_simulator;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;
import java.net.SocketException;

import tc.tlouro_c.stock_exchange_simulator.orders.Order;
import tc.tlouro_c.stock_exchange_simulator.orders.OrderService;

public class BrokerController {

	private BrokerView brokerView;
	private OrderService orderService;
	private ExecutorService processRequestsThreadPool;
	private Selector selector;
	private SocketChannel channel;
	private int port;
	private Order currentWritingOrder;

	public BrokerController() {
		brokerView = new BrokerView();
		orderService = OrderService.getInstance();
		processRequestsThreadPool = Executors.newCachedThreadPool();
	}

	public void startListening(int port) {
		this.port = port;
		try {
			configureClientSocket();
			while (Broker.getConnection() == Connection.ALIVE || Broker.getConnection() == Connection.UNSET) {
				selector.select();
				var keys = selector.selectedKeys();
				var keysIt = keys.iterator();
				while (keysIt.hasNext()) {
					var key = keysIt.next();
					keysIt.remove();
					handleKey(key);
				}
				if (currentWritingOrder == null && orderService.hasOrdersInQueue()) {
					currentWritingOrder = orderService.getNextOrder();
					channel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
				}
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		Broker.setConnection(Connection.DEAD);
	}

	private void handleKey(SelectionKey key) {

		if (key.isValid() && key.isReadable()) {
			read();
		}
		if (key.isValid() && key.isWritable()) {
			write();
		} else if (key.isValid() && key.isConnectable()) {
			connect();
		}
	}

	private void write() {
		try {
			var buffer = currentWritingOrder.getBuffer();
			channel.write(buffer);
			if (!buffer.hasRemaining()) {
				orderService.addToPendingOrders(currentWritingOrder);
				currentWritingOrder = orderService.getNextOrder();
			}
			if (currentWritingOrder == null) {
				channel.register(selector, SelectionKey.OP_READ);
			}
		} catch (SocketException e) {
			Broker.setConnection(Connection.DEAD);
			brokerView.lostConnectionMessage();
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}

	private void read() {
		try {
			var buffer = ByteBuffer.allocate(128);
			var bytesRead = channel.read(buffer);
			if (bytesRead == -1) {
				channel.close();
				Broker.setConnection(Connection.DEAD);
				brokerView.lostConnectionMessage();
			} else {
				buffer.flip();
				if (Broker.getId() == "undefined") {
					retrieveAssignedId(buffer);
				} else {
					processRequestsThreadPool.submit(() -> 
						orderService.processOrderResult(buffer, brokerView));
				}
			}
		} catch (SocketException e) {
			Broker.setConnection(Connection.DEAD);
			brokerView.lostConnectionMessage();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void retrieveAssignedId(ByteBuffer buffer) {
		String bufferString = StandardCharsets.UTF_8.decode(buffer).toString().trim();
		System.out.println(bufferString);
		var idStart = bufferString.indexOf("Assigned ID: ") + "Assigned ID: ".length();
		var idEnd = bufferString.indexOf("\n", idStart);
		Broker.setId(bufferString.substring(idStart, idEnd));
	}

	private void configureClientSocket() throws IOException {
		selector = Selector.open();
		channel = SocketChannel.open();
		channel.configureBlocking(false);
		channel.connect(new InetSocketAddress("localhost", port));
		channel.register(selector, SelectionKey.OP_CONNECT);
	}

	private void connect() {

		try {
			if (channel.finishConnect()) {
				Broker.setConnection(Connection.ALIVE);
				channel.register(selector, SelectionKey.OP_READ);
				brokerView.successfulConnectionMessage();
			} else {
				brokerView.stillConnectingMessage();
			}
		} catch (Exception e) {
			Broker.setConnection(Connection.DEAD);
			brokerView.failedConnectionMessage();
		}
	}

	public void wakeUpSelector() {
		selector.wakeup();
	}

	public Selector getSelector() {
		return selector;
	}
}
