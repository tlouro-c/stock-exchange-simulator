package tc.tlouro_c.stock_exchange_simulator;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FixRequest {

	private Map<String, String> map;
	private ByteBuffer buffer;
	private String request;
	private String soh = "\u0001";

	public FixRequest(ByteBuffer buffer) {
		this.buffer = buffer;
		this.request = StandardCharsets.UTF_8.decode(buffer).toString().trim();
	}

	public FixRequest() {
		this.map = new HashMap<>();
	}

	public ByteBuffer toByteBuffer() {
		var stringBuilder = new StringBuilder();

		stringBuilder.append("49=" + getSenderId() + soh);
		map.remove("49");
		for (var entry : map.entrySet()) {
			stringBuilder.append(entry.getKey());
			stringBuilder.append("=");
			stringBuilder.append(entry.getValue());
			stringBuilder.append(soh);
		}
		var checksum = stringBuilder.toString().chars().sum() % 256;
		stringBuilder.append("10=" + checksum + soh);
		return ByteBuffer.wrap(stringBuilder.toString().getBytes());
	}

	public void parse() throws InvalidFormatException  {
		try {
			this.map = Stream.of(request.split(soh))
							.map(pair -> pair.split("="))
							.filter(entry -> entry.length == 2)
							.collect(Collectors.toMap(
								entry -> entry[0],
								entry -> entry[1]
							));
		} catch (Exception e) {
			throw new InvalidFormatException();
		}
	}

	public void validateChecksum() throws InvalidChecksumException {
		int checksumIndex = request.lastIndexOf("10=");
		String checksum = getChecksum();
		String toSum = request.substring(0, checksumIndex);
		if (Integer.parseInt(checksum) != toSum.chars().sum() % 256) {
			throw new InvalidChecksumException();
		}
	}

	public ByteBuffer getBuffer() {
		return buffer;
	}

	public String getChecksum() {
		var checksum = map.containsKey("10") ? map.get("10") : "";
		return checksum;
	}

	public String getSenderId() {
		var senderId = map.containsKey("49") ? map.get("49") : "";
		return senderId;
	}

	public void setSenderId(String senderId) {
		map.put("49", senderId);
	}

	public String getTargetId() {
		var targetId = map.containsKey("56") ? map.get("56") : "";
		return targetId;
	}

	public void setTargetId(String targetId) {
		map.put("56", targetId);
	}

	public int getOrderType() {
		try {
			return Integer.parseInt(map.get("40"));
		} catch (Exception e) {
			return 0;
		}
	}

	public void setOrderType(String orderType) {
		map.put("40", orderType);
	}

	public String getInstrument() {
		var instrument = map.containsKey("55") ? map.get("55") : "";
		return instrument;
	}

	public void setInstrument(String instrument) {
		map.put("55", instrument);
	}

	public double getPricePerShare() {
		try {
			return Double.parseDouble(map.get("44"));
		} catch (Exception e) {
			return 0;
		}
	}

	public void setPricePerShare(double pricePerShare) {
		map.put("44", String.valueOf(pricePerShare));
	}

	public int getSharesAmount() {
		try {
			return Integer.parseInt(map.get("38"));
		} catch (Exception e) {
			return 0;
		}
	}

	public void setSharesAmount(int sharesAmount) {
		map.put("38", String.valueOf(sharesAmount));
	}

	public String getMarketId() {
		var marketId = map.containsKey("100") ? map.get("100") : "";
		return marketId;
	}

	public void setMarketId(String marketId) {
		map.put("100", marketId);
	}

	public String getState() {
		var state = map.containsKey("39") ? map.get("39") : "";
		return state;
	}

	public void setState(String state) {
		map.put("39", state);
	}

	public void setClientOrderId(int clientOrderId) {
		map.put("11", String.valueOf(clientOrderId));
	}

	public int getClientOrderId() {
		try {
			return Integer.parseInt(map.get("11"));
		} catch (Exception e) {
			return 0;
		}
	}

	public void printMap() {
		map.forEach((k, v) -> System.out.println(k + "=" + v));
	}

	public static class InvalidChecksumException extends Exception {

		public InvalidChecksumException(String message) {
			super(message);
		}
		
		public InvalidChecksumException() {
			super("Invalid checksum");
		}
	}

	public static class InvalidFormatException extends Exception {

		public InvalidFormatException(String message) {
			super(message);
		}
		
		public InvalidFormatException() {
			super("Invalid format");
		}
	}
}
