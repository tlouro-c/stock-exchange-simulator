package tc.tlouro_c.stock_exchange_simulator.market.transactions.handlers;

import tc.tlouro_c.stock_exchange_simulator.market.MarketController;
import tc.tlouro_c.stock_exchange_simulator.market.transactions.Transaction;

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
