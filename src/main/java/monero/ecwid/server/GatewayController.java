package monero.ecwid.server;

import java.math.BigDecimal;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import monero.ecwid.model.EcwidPaymentData;
import monero.ecwid.model.EcwidPaymentDataDecoder;
import monero.ecwid.server.config.ServerConfig;
import monero.ecwid.server.config.ServerConfigFileReader;
import monero.ecwid.server.error.PaymentRequestAlreadyExistsException;
import monero.ecwid.server.repository.PaymentRequestEntity;
import monero.ecwid.server.service.MailService;
import monero.ecwid.server.service.PaymentRequestService;

import org.springframework.ui.Model;


@Controller
public class GatewayController {
    private static final Logger logger = LoggerFactory.getLogger(GatewayController.class);

    @Autowired
    private final PaymentRequestService paymentRequestService;

    @Autowired
    private final MailService mailService;

    public GatewayController(PaymentRequestService paymentRequestService, MailService mailService) {
        this.paymentRequestService = paymentRequestService;
        this.mailService = mailService;
    }

    private static ServerConfig getServerConfig() {
        try {
            return ServerConfigFileReader.read();
        }
        catch (Exception e) {
            return new ServerConfig();
        }
    }

    private static String getClientSecret() {
        return getServerConfig().clientSecret;
    }

    private static Long getRequiredConfirmations() {
        return getServerConfig().requiredConfirmations;
    }

    private void processModel(PaymentRequestEntity request, Model model) {
        BigDecimal piconeroDivider = BigDecimal.valueOf(1000000000000l);
        BigDecimal xmrAmount = BigDecimal.valueOf(request.getAmountToPay().longValue()).divide(piconeroDivider);
        String returnUrl = request.getReturnUrl();
        String txId = request.getTxId();

        model.addAttribute("title", "Monero Payment | Order " + txId);
        model.addAttribute("returnUrl", returnUrl);
        model.addAttribute("address", request.getAddress());
        model.addAttribute("amountXmr", xmrAmount + " XMR");
        model.addAttribute("amountDeposited", BigDecimal.valueOf(request.getAmountDeposited().longValue()).divide(piconeroDivider).toString() + " XMR");
        model.addAttribute("txId", txId);
        model.addAttribute("amountUsd", request.getAmountUsd() + " USD");
        model.addAttribute("paymentStatus", request.getStatus());
        model.addAttribute("createdAt", request.getCreatedAt().toString());
        model.addAttribute("customerMail", request.getCustomerMail());
    }

    private String processPaymentRequest(EcwidPaymentData paymentData, Model model) {
        String txId = paymentData.cart.order.referenceTransactionId;
        PaymentRequestEntity request = null;

        try {
            request = paymentRequestService.newPaymentRequest(paymentData);
            
            try {
                mailService.sendInvoince(request);
            }
            catch (Exception e) {
                logger.warn("Could not send invoice email");
            }
        }
        catch (Exception e) {
            if (e instanceof PaymentRequestAlreadyExistsException) {
                Optional<PaymentRequestEntity> req = paymentRequestService.repository.findById(txId);

                if (!req.isEmpty()) {
                    request = req.get();
                    logger.info("already existing request status: " + request.getStatus() + ", created at: " + request.getCreatedAt().toString());
                    processModel(request, model);
                    return "payment.html";
                }
            }

            logger.error(txId, e);
            model.addAttribute("error", e.getMessage());

            return "error.html";
        }

        logger.info("request status: " + request.getStatus() + ", created at: " + request.getCreatedAt().toString());

        processModel(request, model);

        return "payment.html";
    }

    @PostMapping(path = "/v1/monero/ecwid", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String onPayment(@RequestParam String data, Model model) {
        EcwidPaymentData paymentData;

        try {
            String clientSecret = getClientSecret();

            paymentData = EcwidPaymentDataDecoder.decode(data, clientSecret);
        }
        catch(Exception e) {
            logger.error(e.getMessage());
            model.addAttribute("error", e.getMessage());

            return "error.html";
        }

        return processPaymentRequest(paymentData, model);
    }

    @PostMapping(path = "/v1/monero/ecwid/test")
    public String onTestPayment(@RequestBody(required = true) EcwidPaymentData paymentData, Model model) {
        return processPaymentRequest(paymentData, model);
    }

    @GetMapping(path = "/v1/monero/ecwid/payment")
    public String getPaymentPage(@RequestParam(required = true) String id, Model model) {
        PaymentRequestEntity request;

        try {
            Optional<PaymentRequestEntity> result = paymentRequestService.repository.findById(id);

            if (result.isEmpty()) {
                throw new Exception("Order " + id + " not found");
            }

            request = result.get();
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
            model.addAttribute("error", e.getMessage());

            return "error.html";
        }

        processModel(request, model);

        return "payment.html"; // Nome del file HTML nella cartella `src/main/resources/templates`
    }

    @PostMapping(path = "/v1/monero/ecwid/getPayment")
    @ResponseBody
    public PaymentRequestEntity getPayment(@RequestParam(required = true) String id) throws Exception {
        Optional<PaymentRequestEntity> req = paymentRequestService.repository.findById(id);

        if (req.isPresent()) {
            PaymentRequestEntity paymentReq = req.get();

            paymentReq.setBlockchainHeight(paymentRequestService.wallet.getDaemonHeight());
            paymentReq.setConfirmations(paymentRequestService.getTxConfirmations(id));
            paymentReq.setRequiredConfirmations(getRequiredConfirmations());

            return paymentReq;
        }

        throw new Exception("Request " + id + " not found");
    }
    
}
