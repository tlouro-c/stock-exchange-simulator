package tc.tlouro_c.stock_exchange_simulator.router;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import tc.tlouro_c.stock_exchange_simulator.FixRequest;
import tc.tlouro_c.stock_exchange_simulator.router.handlers.*;
import tc.tlouro_c.utils.Logger;

public class PortsListener {

	private Router router;
	private ConcurrentHashMap<SocketChannel, ConcurrentLinkedDeque<ByteBuffer>> channelPendingData;
	private ConcurrentLinkedQueue<SocketChannel> toWriteQueue;
	private ExecutorService processRequestsThreadPool;
	private Selector selector;

	public PortsListener(Router router) {
		this.router = router;
		this.processRequestsThreadPool = Executors.newCachedThreadPool();
		this.channelPendingData = new ConcurrentHashMap<>();
		this.toWriteQueue = new ConcurrentLinkedQueue<>();
	}

	public void startListening(List<Integer> ports) {
		try {
			this.selector = Selector.open();
			for (int port : ports) {
				configureServerSocket(port);
			}
			Logger.INFO("Listening for connections on ports: " + ports);
		} catch (Exception e) {
			System.err.println(e.getMessage());
			return;
		}

		while (true) {
			try {
				selector.select();
				while (!toWriteQueue.isEmpty()) {
					toWriteQueue.poll().register(selector, SelectionKey.OP_WRITE);
				}
			} catch (Exception e) {
				System.err.println(e.getMessage());
				continue;
			}
			var keys = selector.selectedKeys();
			var keysIterator = keys.iterator();
			while (keysIterator.hasNext()) {
				var key = keysIterator.next();
				keysIterator.remove();
				handleKey(key);
			}
		}
	}

	private void handleKey(SelectionKey key) {

		if (key.isValid() && key.isAcceptable()) {
			accept(key);
		}
		if (key.isValid() && key.isReadable()) {
			read(key);
		}
		if (key.isValid() && key.isWritable()) {
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
				router.removeRoute(channel);
				channelPendingData.remove(channel);
				Logger.INFO("Connection closed for: " + channel.getRemoteAddress());
				Logger.INFO("Routing table:\n" + router.getRoutingTable());
				channel.close();
			} else {
				Logger.INFO("Received message from: " + channel.getRemoteAddress());
				buffer.flip();
				processRequestsThreadPool.submit(() -> process(channel, buffer, this));
			}
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}

	private void process(SocketChannel channel, ByteBuffer buffer, PortsListener portsListener) {

		var requestProcessingChain = new ValidateChecksum()
					.setNextHandler(new IdentifyDestination())
					.setNextHandler(new ForwardRequest());

		var request = new FixRequest(buffer);
		try {
			requestProcessingChain.handleRequest(channel, request, portsListener);
		} catch (Exception e) {
			buffer = ByteBuffer.wrap((e.getMessage() + "\n").getBytes());
			portsListener.addToWriteQueue(channel);
			portsListener.addToPendingData(channel, buffer);
		}
		selector.wakeup();
	}

	private void accept(SelectionKey key) {

		ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
		try {
			SocketChannel newConnectionChannel = serverSocketChannel.accept();
			newConnectionChannel.configureBlocking(false);
			newConnectionChannel.register(selector, SelectionKey.OP_WRITE);
			var assignedId = router.getNewId();
			router.addRoute(assignedId, newConnectionChannel);
			channelPendingData.put(newConnectionChannel, new ConcurrentLinkedDeque<>());
			
			var buffer = ByteBuffer.wrap(String.format(
				"You have been successfully connected.\n" + 
				"Assigned ID: %d\n" +
				"Use this ID for further communication.\n", 
				assignedId
			).getBytes());
			var pendingDataQueue = channelPendingData.get(newConnectionChannel);
			pendingDataQueue.add(buffer);
			Logger.INFO("New connection from " + newConnectionChannel.getRemoteAddress());
			Logger.INFO("Routing table:\n" + router.getRoutingTable());
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}

	private void configureServerSocket(int port) throws IOException {
		ServerSocketChannel ssc = ServerSocketChannel.open();
		ssc.configureBlocking(false);
		ssc.bind(new InetSocketAddress("localhost", port));
		ssc.register(selector, SelectionKey.OP_ACCEPT);
	}

	public void addToWriteQueue(SocketChannel channel) {
		toWriteQueue.add(channel);
	}

	public void addToPendingData(SocketChannel channel, ByteBuffer buffer) {
		var pendingDataQueue = channelPendingData.get(channel);
		pendingDataQueue.add(buffer);
	}

	public SocketChannel fetchFromRoutingTable(int id) {
		return router.getRoute(id);
	}
}
