package monero.ecwid.server.utils;

import java.math.BigInteger;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import monero.ecwid.server.repository.MoneroTransactionEntity;
import monero.ecwid.server.repository.PaymentRequestEntity;
import monero.ecwid.server.service.PaymentRequestService;
import monero.wallet.model.MoneroOutputWallet;
import monero.wallet.model.MoneroWalletListener;

public class WalletListener extends MoneroWalletListener {
    private static final Logger logger = LoggerFactory.getLogger(WalletListener.class);

    private PaymentRequestService paymentRequestService;

    public WalletListener(PaymentRequestService paymentRequestService) {
        this.paymentRequestService = paymentRequestService;
    }

    private void processTransaction(MoneroOutputWallet output) {
        BigInteger amount = output.getAmount();
        Long height = output.getTx().getHeight();
        String txHash = output.getTx().getHash();
        Boolean isConfirmed = output.getTx().isConfirmed();
        Long confirmations = output.getTx().getNumConfirmations();

        Optional<MoneroTransactionEntity> moneroTransaction = this.paymentRequestService.transactionRepository.findById(txHash);

        if (!moneroTransaction.isEmpty()) {
            MoneroTransactionEntity tx = moneroTransaction.get();

            if (tx.getHeight().equals(0l) && isConfirmed) {
                tx.setHeight(height);
                this.paymentRequestService.transactionRepository.save(tx);
                logger.info("Confirmed transaction: " + tx.getTxHash() + ", confirmations: " + confirmations);
            }
            else {
                logger.info("Unconfirmed transaction: " + tx.getTxHash());
            }

            return;
        }

        String address = this.paymentRequestService.wallet.getSubaddress(output.getAccountIndex(), output.getSubaddressIndex()).getAddress();
        
        logger.info("" + amount.divide(BigInteger.valueOf(1000000000000l)) + " XMR received at address: " + address);

        PaymentRequestEntity req = this.paymentRequestService.repository.findByAddress(address);

        if (req == null) {
            logger.info("No payment request found for address " + address);
            return;
        }

        MoneroTransactionEntity transaction = new MoneroTransactionEntity();
        
        transaction.setTxHash(txHash);
        transaction.setTxId(req.getTxId());
        transaction.setAmount(amount);
        
        if (isConfirmed) {
            transaction.setHeight(height);
        }
        else {
            transaction.setHeight(0l);
        }
        
        try {
            this.paymentRequestService.transactionRepository.save(transaction);
            logger.info("Successfully recorded tx " + txHash);
        }
        catch (Exception e) {
            logger.error("Could not update order " + req.getTxId() + " status", e);
        }

    }
    
    @Override
    public void onOutputReceived(MoneroOutputWallet output) {
        processTransaction(output);
    }

    @Override
    public void onSyncProgress(long height, long startHeight, long endHeight, double percentDone, String message) {
        logger.info(message + " " + percentDone*100 + "% (" + height + "/" + endHeight + ")");
    }
}
