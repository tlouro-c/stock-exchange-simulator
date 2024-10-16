package tc.tlouro_c.stock_exchange_simulator.router;

import java.nio.channels.SocketChannel;
import java.util.concurrent.ConcurrentHashMap;

public class RoutingTable extends ConcurrentHashMap<Integer, SocketChannel> {

	private ConcurrentHashMap<SocketChannel, Integer> invertedMap = new ConcurrentHashMap<>();

	public void registerRoute(Integer key, SocketChannel value) {
		invertedMap.put(value, key);
		super.put(key, value);
	}

	public void unregisterRoute(SocketChannel socket) {
		Integer key = invertedMap.remove(socket);
		super.remove(key);
	}

	public SocketChannel getSocket(Integer id) {
		return super.get(id);
	}

	@Override
	public String toString() {
		var result = "";
		for (var entry : this.entrySet()) {
			result += entry.getKey() + " -> ";
			try {
				var channelLocalAddress = entry.getValue().getLocalAddress();
				result += channelLocalAddress == null ? "unbound" : channelLocalAddress + "\n";
			} catch (Exception e) {
				result += "closed\n";
			}
		}
		return result;
	}
	
}
