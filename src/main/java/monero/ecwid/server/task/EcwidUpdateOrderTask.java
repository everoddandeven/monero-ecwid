package monero.ecwid.server.task;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import monero.ecwid.model.EcwidStoreService;
import monero.ecwid.server.repository.MoneroTransactionEntity;
import monero.ecwid.server.repository.PaymentRequestEntity;
import monero.ecwid.server.service.PaymentRequestService;
import monero.ecwid.server.utils.WalletUtils;

@Component
public class EcwidUpdateOrderTask implements ServerTask {
    private static final Logger logger = LoggerFactory.getLogger(EcwidUpdateOrderTask.class);
    private final PaymentRequestService paymentRequestService;

    public EcwidUpdateOrderTask(PaymentRequestService service) {
        paymentRequestService = service;
    }

    private Long getRequiredConfirmations() {
        try {
            return Long.valueOf(10l);
        }
        catch (Exception e) {
            return Long.valueOf(10l);
        }
    }

    private Long getTxConfirmations(String txHash) {
        Long confirmations = WalletUtils.getWallet().getTx(txHash).getNumConfirmations();

        if (confirmations == null) {
            return Long.valueOf(0l);
        }

        return confirmations;
    }

    private EcwidStoreService getStore(PaymentRequestEntity paymentRequest) {
        return EcwidStoreService.getService(paymentRequest.getStoreId(), paymentRequest.getStoreToken());
    }

    private boolean waitingForConfirmation(PaymentRequestEntity paymentRequest) {
        String txId = paymentRequest.getTxId();

        List<MoneroTransactionEntity> txs = paymentRequestService.transactionRepository.findAll();
        MoneroTransactionEntity transaction = null;

        for (MoneroTransactionEntity tx : txs) {
            if (!tx.getTxId().equals(txId)) {
                continue;
            }
            if (transaction == null) {
                transaction = tx;
            }
            else if (transaction.getHeight().compareTo(tx.getHeight()) < 0) {
                transaction = tx;
            }
        }

        if (transaction == null) {            
            if (paymentRequest.getStatus().equals("PAID")) {
                logger.info("waitingForConfirmation(): No monero_transactions records for paid payment request " + paymentRequest.getTxId());
                return true;
            }

            return false;
        }

        Long requiredConfirmations = getRequiredConfirmations();
        Long txConfirmations = getTxConfirmations(transaction.getTxHash());

        return txConfirmations.compareTo(requiredConfirmations) < 0;
    }

    private void updatePaymentRequest(PaymentRequestEntity paymentRequest) {
        try {
            if (paymentRequest.getEcwidApiUpdated()) {
                return;
            }
    
            String txId = paymentRequest.getTxId();
            String status = paymentRequest.getStatus();
            EcwidStoreService storeService = getStore(paymentRequest);
            boolean updated = false;
    
            if (status.equals("EXPIRED")) {
                storeService.setOrderCancelled(txId);
                updated = true;
            }
            else if (status.equals("PAID")) {
                if (!waitingForConfirmation(paymentRequest)) {
                    storeService.setOrderPaid(txId);
                    updated = true;
                }
                else {
                    logger.info("Waiting for order confirmation before updating ecwid API");
                }
            }
            else if (status.equals("CANCELLED")) {
                storeService.setOrderCancelled(txId);
                updated = true;
            }
    
            if (updated) {
                paymentRequest.setEcwidApiUpdated(true);
                paymentRequestService.repository.save(paymentRequest);
                logger.info("Successfully update ecwid payment request " + txId + " status: " + status);
            }
        }
        catch (Exception e) {
            logger.error("Could not update payment request", e);
        }

    }

    private void updatePaymentRequests() {
        List<PaymentRequestEntity> requests = paymentRequestService.repository.findAll();

        for (PaymentRequestEntity paymentRequest : requests) {
            updatePaymentRequest(paymentRequest);
        }
    }

    @Scheduled(fixedRate = 5000)
    @Override
    public void execute() {
        try {
            updatePaymentRequests();            
            logger.info("Successfully executed update ecwid orders task");
        }
        catch (Exception e) {
            logger.error("An error occurred while updating ecwid orders", e);
        }
    }

}
