package monero.ecwid.server.task;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import monero.ecwid.model.EcwidStoreService;
import monero.ecwid.server.config.ServerConfigFileReader;
import monero.ecwid.server.repository.PaymentRequestEntity;
import monero.ecwid.server.service.MailService;
import monero.ecwid.server.service.PaymentRequestService;

@Component
public class EcwidUpdateOrderTask implements ServerTask {
    private static final Logger logger = LoggerFactory.getLogger(EcwidUpdateOrderTask.class);
    private final PaymentRequestService paymentRequestService;
    private final MailService mailService;

    public EcwidUpdateOrderTask(PaymentRequestService service, MailService mailService) {
        paymentRequestService = service;
        this.mailService = mailService;
    }

    private static Long getRequiredConfirmations() {
        try {
            return ServerConfigFileReader.read().requiredConfirmations;
        }
        catch (Exception e) {
            return Long.valueOf(10l);
        }
    }

    private EcwidStoreService getStore(PaymentRequestEntity paymentRequest) {
        return EcwidStoreService.getService(paymentRequest.getStoreId(), paymentRequest.getStoreToken());
    }

    private boolean waitingForConfirmation(PaymentRequestEntity paymentRequest) {
        String txId = paymentRequest.getTxId();

        Long requiredConfirmations = getRequiredConfirmations();
        Long txConfirmations = paymentRequestService.getTxConfirmations(txId);

        return txConfirmations.compareTo(requiredConfirmations) < 0;
    }

    private boolean updatePaymentRequest(PaymentRequestEntity paymentRequest) {
        try {
            if (paymentRequest.getEcwidApiUpdated()) {
                return false;
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

                    if (mailService.isEnabled()) {
                        mailService.sendReceipt(paymentRequest);
                    }

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
                
                return true;
            }
        }
        catch (Exception e) {
            logger.error("Could not update payment request", e);
        }

        return false;
    }

    private void updatePaymentRequests() {
        List<PaymentRequestEntity> requests = paymentRequestService.repository.findAll();
        boolean updated = false;

        for (PaymentRequestEntity paymentRequest : requests) {
            if (updatePaymentRequest(paymentRequest)) {
                updated = true;
            }
        }

        if (updated) {
            logger.info("Successfully executed update ecwid orders task");
        }
    }

    @Scheduled(fixedRate = 5000)
    @Override
    public void execute() {
        try {
            updatePaymentRequests();            
        }
        catch (Exception e) {
            logger.error("An error occurred while updating ecwid orders", e);
        }
    }

}
