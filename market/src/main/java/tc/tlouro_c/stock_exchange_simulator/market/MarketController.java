package tc.tlouro_c.stock_exchange_simulator.market;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.net.SocketException;

import java.time.Duration;
import java.time.Instant;

import tc.tlouro_c.stock_exchange_simulator.Connection;
import tc.tlouro_c.stock_exchange_simulator.market.stocks.StockDAO;
import tc.tlouro_c.stock_exchange_simulator.market.transactions.TransactionService;

public class MarketController {

	private MarketView marketView;
	private TransactionService transactionsService;
	private ExecutorService processRequestsThreadPool;
	private ExecutorService stockPriceUpdaterThread;
	private ConcurrentLinkedQueue<ByteBuffer> pendingResponses;
	private Selector selector;
	private SocketChannel channel;
	private int port;
	private ByteBuffer currentResponse;

	public MarketController() {
		marketView = new MarketView();
		transactionsService = TransactionService.getInstance();
		processRequestsThreadPool = Executors.newCachedThreadPool();
		stockPriceUpdaterThread = Executors.newSingleThreadExecutor();
		pendingResponses = new ConcurrentLinkedQueue<>();
	}

	public void startListening(int port) {
		this.port = port;
		try {
			configureClientSocket();
			var timeMarker = Instant.now();
			while (Market.getConnection() == Connection.ALIVE || Market.getConnection() == Connection.UNSET) {
				selector.select(5000);
				var keys = selector.selectedKeys();
				var keysIt = keys.iterator();
				while (keysIt.hasNext()) {
					var key = keysIt.next();
					keysIt.remove();
					handleKey(key);
				}
				if (currentResponse == null && !pendingResponses.isEmpty()) {
					currentResponse = pendingResponses.poll();
					channel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
				}
				if (Duration.between(timeMarker, Instant.now()).compareTo(Duration.ofSeconds(5)) > 0) {
					timeMarker = Instant.now();
					stockPriceUpdaterThread.submit(() -> StockDAO.getInstance().simulateMarketActivity(marketView));
				}
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		processRequestsThreadPool.shutdown();
		stockPriceUpdaterThread.shutdownNow();
		Market.setConnection(Connection.DEAD);
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
			channel.write(currentResponse);
			if (!currentResponse.hasRemaining()) {
				currentResponse = pendingResponses.poll();
			}
			if (currentResponse == null) {
				channel.register(selector, SelectionKey.OP_READ);
			}
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
				Market.setConnection(Connection.DEAD);
				marketView.lostConnectionMessage();
			} else {
				buffer.flip();
				if (Market.getId() == "undefined") {
					retrieveAssignedId(buffer);
					transactionsService.init();
				} else {
					processRequestsThreadPool.submit(() -> 
						transactionsService.handleTransaction(this, buffer));
				}
			}
		} catch (SocketException e) {
			Market.setConnection(Connection.DEAD);
			marketView.lostConnectionMessage();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void retrieveAssignedId(ByteBuffer buffer) {
		String bufferString = StandardCharsets.UTF_8.decode(buffer).toString().trim();
		System.out.println(bufferString);
		var idStart = bufferString.indexOf("Assigned ID: ") + "Assigned ID: ".length();
		var idEnd = bufferString.indexOf("\n", idStart);
		Market.setId(bufferString.substring(idStart, idEnd));
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
				Market.setConnection(Connection.ALIVE);
				channel.register(selector, SelectionKey.OP_READ);
				marketView.successfulConnectionMessage();
			} else {
				marketView.stillConnectingMessage();
			}
		} catch (Exception e) {
			Market.setConnection(Connection.DEAD);
			marketView.failedConnectionMessage();
		}
	}

	public void addToPendingResponses(ByteBuffer buffer) {
		pendingResponses.add(buffer);
	}

	public void wakeUpSelector() {
		selector.wakeup();
	}

	public MarketView getMarketView() {
		return marketView;
	}
}
