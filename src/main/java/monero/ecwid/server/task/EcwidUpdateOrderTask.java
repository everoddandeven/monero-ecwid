package monero.ecwid.server.task;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import monero.ecwid.model.EcwidStoreService;
import monero.ecwid.server.repository.PaymentRequestEntity;
import monero.ecwid.server.service.PaymentRequestService;

@Component
public class EcwidUpdateOrderTask implements ServerTask {
    private static final Logger logger = LoggerFactory.getLogger(EcwidUpdateOrderTask.class);
    private final PaymentRequestService paymentRequestService;

    public EcwidUpdateOrderTask(PaymentRequestService service) {
        paymentRequestService = service;
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

    @Scheduled(fixedRate = 5000)
    public void execute() {
        try {
            refreshPaymentRequests();            
            logger.info("Successfully executed update ecwid orders task");
        }
        catch (Exception e) {
            logger.error("An error occurred while updating ecwid orders", e);
        }
    }

}
