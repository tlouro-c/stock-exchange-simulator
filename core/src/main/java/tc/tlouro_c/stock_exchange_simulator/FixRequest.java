package tc.tlouro_c.stock_exchange_simulator;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FixRequest {

	private Map<String, String> map;
	private ByteBuffer buffer;
	private String request;
	private String soh;

	public FixRequest(ByteBuffer buffer) {
		this.soh = "\u0001";
		this.buffer = buffer;
		this.request = StandardCharsets.UTF_8.decode(buffer).toString().trim();
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
		return map.get("10");
	}

	public String getSenderId() {
		return map.get("49");
	}

	public String getTargetId() {
		return map.get("56");
	}

	public String getOrderType() {
		return map.get("40");
	}

	public String getInstrument() {
		return map.get("55");
	}

	public int getQuantity() {
		return Integer.parseInt(map.get("38"));
	}

	public String getMarketId() {
		return map.get("100");
	}

	public String getState() {
		return map.get("39");
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
