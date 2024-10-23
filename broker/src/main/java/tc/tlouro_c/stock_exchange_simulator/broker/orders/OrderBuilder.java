package tc.tlouro_c.stock_exchange_simulator.broker.orders;

import javax.validation.ConstraintViolationException;
import javax.validation.Validation;

public class OrderBuilder {

	private Order order;

	public OrderBuilder(Order order) {
		this.order = order;
	}

	public OrderBuilder instrument(String instrument) {
		order.setInstrument(instrument);
		return this;
	}

	public OrderBuilder market(String market) {
		order.setMarket(market);
		return this;
	}

	public OrderBuilder limitPricePerShare(double limitPricePerShare) {
		order.setLimitPricePerShare(limitPricePerShare);
		return this;
	}

	public OrderBuilder shares(int shares) {
		order.setShares(shares);
		return this;
	}

	public Order build() {
		var validatorFactory = Validation.buildDefaultValidatorFactory();
		var validator = validatorFactory.getValidator();
		var violations = validator.validate(order);

		if (!violations.isEmpty()) {
			throw new ConstraintViolationException("Order is incomplete", violations);
		}
		order.setBuffer(Order.toFix(order).toByteBuffer());
		return order;
	}
}
