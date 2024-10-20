package tc.tlouro_c.stock_exchange_simulator.transactions.handlers;

import javax.validation.ConstraintViolationException;
import javax.validation.Validation;

import tc.tlouro_c.stock_exchange_simulator.FixRequest;
import tc.tlouro_c.stock_exchange_simulator.transactions.Transaction;

public class ValidateTransactionFields extends TransactionHandler {

	@Override
	public void handleTransaction(Transaction transaction, Object extra)
			throws TransactionRejectedException, TransactionAcceptedException {

		try {
			var fixRequest = (FixRequest) extra;
			transaction = Transaction.fromFix(fixRequest);

			var validatorFactory = Validation.buildDefaultValidatorFactory();
			var validator = validatorFactory.getValidator();
			var violations = validator.validate(transaction);
			if (!violations.isEmpty()) {
				throw new ConstraintViolationException(violations);
			}

			nextHandler.handleTransaction(transaction, null);
		} catch (TransactionRejectedException e) {
			throw e;
		} catch (TransactionAcceptedException e) {
			throw e;
		} catch (Exception e) {
			throw new TransactionRejectedException(e.getMessage(), transaction);
		}
	}
	
}
