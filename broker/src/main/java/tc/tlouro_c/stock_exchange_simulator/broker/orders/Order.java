package tc.tlouro_c.stock_exchange_simulator.broker.orders;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

import java.nio.ByteBuffer;

import tc.tlouro_c.stock_exchange_simulator.broker.Broker;
import tc.tlouro_c.stock_exchange_simulator.FixRequest;


public abstract class Order {

	private static int ordersCount = 0;

	private int id;
	@NotBlank
	private String instrument;
	@NotBlank
	private String market;
	@Positive
	private double limitPricePerShare;
	@Positive
	private int shares;
	private int side;
	private OrderState state;
	private double realPricePerShare;
	private double total;
	private ByteBuffer buffer;

	protected Order() {
		id = ++ordersCount;
		state = OrderState.NEW;
	}

	public static FixRequest toFix(Order order) {
		var fixRequest = new FixRequest();

		fixRequest.setSenderId(Broker.getId());
		fixRequest.setTargetId(order.market);
		fixRequest.setMarketId(order.market);
		fixRequest.setClientOrderId(order.id);
		fixRequest.setInstrument(order.instrument);
		fixRequest.setOrderType(String.valueOf(order.side));
		fixRequest.setPricePerShare(order.limitPricePerShare);
		fixRequest.setSharesAmount(order.shares);
		fixRequest.setState(String.valueOf(order.state.getValue()));

		return fixRequest;
	}

	public static int getOrdersCount() {
		return ordersCount;
	}
	public static void setOrdersCount(int ordersCount) {
		Order.ordersCount = ordersCount;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getInstrument() {
		return instrument;
	}
	public void setInstrument(String instrument) {
		this.instrument = instrument;
	}
	public String getMarket() {
		return market;
	}
	public void setMarket(String market) {
		this.market = market;
	}
	public double getLimitPricePerShare() {
		return limitPricePerShare;
	}
	public void setLimitPricePerShare(double limitPricePerShare) {
		this.limitPricePerShare = limitPricePerShare;
	}
	public int getShares() {
		return shares;
	}
	public void setShares(int shares) {
		this.shares = shares;
	}
	public int getSide() {
		return side;
	}
	public void setSide(int side) {
		this.side = side;
	}
	public OrderState getState() {
		return state;
	}
	public void setState(OrderState state) {
		this.state = state;
	}

	public ByteBuffer getBuffer() {
		return buffer;
	}

	public void setBuffer(ByteBuffer buffer) {
		this.buffer = buffer;
	}

	public double getRealPricePerShare() {
		return realPricePerShare;
	}

	public void setRealPricePerShare(double realPricePerShare) {
		this.realPricePerShare = realPricePerShare;
		this.total = realPricePerShare * shares;
	}

	public double getTotal() {
		return total;
	}
}
