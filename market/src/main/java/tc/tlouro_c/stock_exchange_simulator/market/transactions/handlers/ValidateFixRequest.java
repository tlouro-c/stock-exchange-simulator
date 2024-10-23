package tc.tlouro_c.stock_exchange_simulator.market.transactions.handlers;

import tc.tlouro_c.stock_exchange_simulator.FixRequest;
import tc.tlouro_c.stock_exchange_simulator.market.transactions.Transaction;

public class ValidateFixRequest extends TransactionHandler {

	@Override
	public void handleTransaction(Transaction transaction,
		Object extra) throws TransactionRejectedException, TransactionAcceptedException {

		try {
			var fixRequest = (FixRequest) extra;
			fixRequest.parse();
			fixRequest.validateChecksum();
			nextHandler.handleTransaction(transaction, fixRequest);
		} catch (TransactionRejectedException e) {
			throw e;
		} catch (TransactionAcceptedException e) {
			throw e;
		} catch (Exception e) {
			throw new TransactionRejectedException(e.getMessage(), transaction);
		}
	}
}
