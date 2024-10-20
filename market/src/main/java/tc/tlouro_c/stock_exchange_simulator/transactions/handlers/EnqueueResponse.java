package tc.tlouro_c.stock_exchange_simulator.transactions.handlers;

import tc.tlouro_c.stock_exchange_simulator.MarketController;
import tc.tlouro_c.stock_exchange_simulator.transactions.Transaction;

public class EnqueueResponse extends TransactionHandler {

	@Override
	public void handleTransaction(Transaction transaction, Object extra)
			throws TransactionRejectedException, TransactionAcceptedException {
		
		var fixRequest = Transaction.toFix(transaction);
		var marketController = (MarketController) extra;

		marketController.addToPendingResponses(fixRequest.toByteBuffer());
		marketController.wakeUpSelector();
	}
	
}
