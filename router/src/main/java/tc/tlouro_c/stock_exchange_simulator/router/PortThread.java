package tc.tlouro_c.stock_exchange_simulator.router;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PortThread implements Runnable {

	private RoutingTable routingTable;
	private ConcurrentHashMap<SocketChannel, ConcurrentLinkedDeque<ByteBuffer>> channelPendingData;
	private ExecutorService processRequestsThreadPool;
	private Selector selector;
	private int port;

	public PortThread(RoutingTable routingTable, int port) {
		this.routingTable = routingTable;
		this.port = port;
		this.processRequestsThreadPool = Executors.newCachedThreadPool();
		this.channelPendingData = new ConcurrentHashMap<>();
	}

	@Override
	public void run() {

		ServerSocketChannel serverSocketChannel = null;
		try {
			configureServerSocket(serverSocketChannel);
		} catch (Exception e) {
			System.err.println(e.getMessage());
			return;
		}

		while (true) {
			try {
				System.out.print("\nWaiting for incoming keys... ");
				selector.select();
			} catch (IOException e) {
				System.err.println(e.getMessage());
				continue;
			}

			var keys = selector.selectedKeys();
			var keysIterator = keys.iterator();
			while (keysIterator.hasNext()) {
				var key = keysIterator.next();
				keysIterator.remove();
				handleKey(key);
				System.out.println("\nKey handled: " + key);
			}
		}
		
	}

	private void handleKey(SelectionKey key) {

		if (!key.isValid()) {
			return;
		}

		if (key.isAcceptable()) {
			accept(key);
		}
		if (key.isReadable()) {
			read(key);
		}
		if (key.isWritable()) {
			write(key);
		}
	}

	private void write(SelectionKey key) {

		SocketChannel channel = (SocketChannel) key.channel();
		var pendingDataQueue = channelPendingData.get(channel);
		if (pendingDataQueue.isEmpty()) {
			return;
		}

		try {
			Iterator<ByteBuffer> queueIterator = pendingDataQueue.iterator();
			while (queueIterator.hasNext()) {
				var buffer = queueIterator.next();
				channel.write(buffer);
				if (buffer.hasRemaining()) {
					buffer.compact();
					return;
				} else {
					queueIterator.remove();
				}
			}
			channel.register(selector, SelectionKey.OP_READ);
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}

	private void read(SelectionKey key) {

		SocketChannel channel = (SocketChannel) key.channel();
		try {
			ByteBuffer buffer = ByteBuffer.allocateDirect(512);
			int bytesRead = channel.read(buffer);
			if (bytesRead == -1) {
				channel.close();
				routingTable.unregisterRoute(channel);
				channelPendingData.remove(channel);
				System.out.println("DISCONNECTED"); //! DEV ONLY
			} else {
				//TODO Implement request processing
				// processRequestsThreadPool.submit(null);
			}
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}

	private void accept(SelectionKey key) {

		ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
		try {
			System.out.println("CONNECTED"); //! DEV ONLY
			SocketChannel newConnectionChannel = serverSocketChannel.accept();
			newConnectionChannel.configureBlocking(false);
			newConnectionChannel.register(selector, SelectionKey.OP_WRITE);
			var assignedId = Router.getAndIncrementId();
			routingTable.registerRoute(assignedId, newConnectionChannel);
			channelPendingData.put(newConnectionChannel, new ConcurrentLinkedDeque<>());
			
			var buffer = ByteBuffer.wrap(String.format(
				"You have been successfully connected.\n" + 
				"Assigned ID: %d\n" +
				"Use this ID for further communication.\n", 
				assignedId
			).getBytes());
			var pendingDataQueue = channelPendingData.get(newConnectionChannel);
			pendingDataQueue.add(buffer);

		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}

	private void configureServerSocket(ServerSocketChannel ssc) throws IOException {
		ssc = ServerSocketChannel.open();
		ssc.configureBlocking(false);
		ssc.bind(new InetSocketAddress("localhost", this.port));
		selector = Selector.open();
		ssc.register(selector, SelectionKey.OP_ACCEPT);
	}
	
}
