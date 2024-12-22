package monero.ecwid.server.utils;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import monero.ecwid.model.EcwidStoreService;
import monero.ecwid.server.repository.MoneroTransactionEntity;
import monero.ecwid.server.repository.PaymentRequestEntity;
import monero.ecwid.server.service.PaymentRequestService;
import monero.wallet.model.MoneroOutputWallet;
import monero.wallet.model.MoneroSubaddress;
import monero.wallet.model.MoneroWalletListener;

public class WalletListener extends MoneroWalletListener {
    private static final Logger logger = LoggerFactory.getLogger(WalletListener.class);

    private PaymentRequestService paymentRequestService;

    public WalletListener(PaymentRequestService paymentRequestService) {
        this.paymentRequestService = paymentRequestService;
    }

    private void refreshPaymentRequests() {
        List<PaymentRequestEntity> requests = paymentRequestService.repository.findAll();

        for (PaymentRequestEntity paymentRequest : requests) {
            String txId = paymentRequest.getTxId();
            
            if (paymentRequest.getStatus().equals("UNPAID") && paymentRequest.isExpired()) {
                EcwidStoreService storeService = EcwidStoreService.getService(paymentRequest.getStoreId(), paymentRequest.getStoreToken());
                
                try {
                    storeService.setOrderCancelled(txId);

                    logger.info("Setting tx " + txId + " as expired");
                    paymentRequest.setStatus("EXPIRED");
                    paymentRequestService.repository.save(paymentRequest);
                }
                catch (Exception e) {
                    logger.error("Could not set order cancelled", e);
                }
            }
        
        }
    }

    private void processOutputTx(MoneroOutputWallet output) {
        BigInteger amount = output.getAmount();
        Long height = output.getTx().getHeight();
        String txHash = output.getTx().getHash();
        Boolean isConfirmed = output.getTx().isConfirmed();
        Boolean isLocked = output.getTx().isLocked();
        Long confirmations = output.getTx().getNumConfirmations();

        Optional<MoneroTransactionEntity> moneroTransaction = this.paymentRequestService.transactionRepository.findById(txHash);

        if (!moneroTransaction.isEmpty()) {
            MoneroTransactionEntity tx = moneroTransaction.get();

            if (tx.getHeight().equals(0l) && isConfirmed) {
                tx.setHeight(height);
                this.paymentRequestService.transactionRepository.save(tx);
            }

            logger.info("output already processed " + amount + ", tx hash: " + txHash + ", confirmed: " + isConfirmed + ", is locked: " + isLocked);
            return;
        }

        logger.info("Received output " + amount + ", tx hash: " + txHash + ", confirmed: " + isConfirmed + ", is locked: " + isLocked + ", confirmations: " + confirmations);

        MoneroSubaddress address = this.paymentRequestService.wallet.getSubaddress(output.getAccountIndex(), output.getSubaddressIndex());

        logger.info("Got address: " + address);

        PaymentRequestEntity req = this.paymentRequestService.repository.findByAddress(address.getAddress());

        if (req == null) {
            logger.info("No payment request found");
            return;
        }

        logger.info("Payment request amount left to pay: " + req.getAmountToPay());

        if (req.getStatus().equals("UNPAID") && req.getAmountToPay().compareTo(amount) <= 0) {
            req.setStatus("PAID");
        }
        else {
            logger.info("Partially paid");
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
        
        // Send update status to ecwid before redirect

        EcwidStoreService storeService = EcwidStoreService.getService(req.getStoreId(), req.getStoreToken());

        try {
            if (req.getStatus() == "PAID") {
                storeService.setOrderPaid(req.getTxId());
                logger.info("Updated ECWID store");
            }

            this.paymentRequestService.transactionRepository.save(transaction);
            this.paymentRequestService.repository.save(req);
        }
        catch (Exception e) {
            logger.error("Could not update order status", e);
        }

    }
    
    @Override
    public void onOutputReceived(MoneroOutputWallet output) {
        processOutputTx(output);
    }

    @Override
    public void onSyncProgress(long height, long startHeight, long endHeight, double percentDone, String message) {
        logger.info(message + " " + percentDone*100 + "%");

        refreshPaymentRequests();
    }
}
