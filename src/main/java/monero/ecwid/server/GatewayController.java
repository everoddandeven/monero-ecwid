package monero.ecwid.server;

import java.math.BigDecimal;
import java.math.BigInteger;
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
import monero.ecwid.server.repository.PaymentRequestEntity;
import monero.ecwid.server.service.PaymentRequestService;
import monero.ecwid.server.utils.XmrConverter;

import org.springframework.ui.Model;


@Controller
public class GatewayController {
    private static final Logger logger = LoggerFactory.getLogger(GatewayController.class);

    @Autowired
    private final PaymentRequestService paymentRequestService;

    public GatewayController(PaymentRequestService paymentRequestService) {
        this.paymentRequestService = paymentRequestService;
    }

    public static BigInteger convertUsdToXmr(Float usdTotal) throws Exception {
        return XmrConverter.convertUsdToPiconero(usdTotal);
    }

    private String processPaymentRequest(EcwidPaymentData paymentData, Model model) {
        String orderId = paymentData.cart.order.id;
        String txId = paymentData.cart.order.referenceTransactionId;
        Float usdTotal = paymentData.cart.order.usdTotal;
        String returnUrl = paymentData.returnUrl;
        String token = paymentData.token;
        Integer storeId = paymentData.storeId;

        logger.info("New payment request: order id: " + orderId + ", tx id: " + txId + ", usd total: " + usdTotal);

        PaymentRequestEntity request;

        try {
            request = paymentRequestService.newPaymentRequest(txId, storeId, token, usdTotal, convertUsdToXmr(usdTotal), returnUrl);
        }
        catch (Exception e) {
            logger.error(txId, e);
            model.addAttribute("error", e.getMessage());

            return "error.html";
        }

        BigDecimal xmrAmount = BigDecimal.valueOf(request.getAmountToPay().longValue()).divide(BigDecimal.valueOf(1000000000000l));
        model.addAttribute("title", "Monero Payment | Order " + txId);
        model.addAttribute("returnUrl", returnUrl);
        model.addAttribute("address", request.getAddress());
        model.addAttribute("amountXmr", xmrAmount + " XMR");
        model.addAttribute("txId", txId);
        model.addAttribute("amountUsd", request.getAmountUsd() + " USD");
        model.addAttribute("status", request.getStatus());

        return "payment.html";
    }

    @PostMapping(path = "/v1/monero/ecwid", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String onPayment(@RequestParam String data, Model model) {
        EcwidPaymentData paymentData;

        try {
            String clientSecret = "Pe087Q6jr0CgwI96R2ZTuUMWfUAHQjMq";

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

        String returnUrl = request.getReturnUrl();

        BigDecimal xmrAmount = BigDecimal.valueOf(request.getAmountToPay().longValue()).divide(BigDecimal.valueOf(1000000000000l));

        model.addAttribute("returnUrl", returnUrl);
        model.addAttribute("title", "Monero Payment | Order " + id);
        model.addAttribute("txId", id);
        model.addAttribute("address", request.getAddress());
        model.addAttribute("amountXmr", xmrAmount + " XMR");
        model.addAttribute("amountUsd", request.getAmountUsd() + " USD");
        model.addAttribute("status", request.getStatus());

        return "payment.html"; // Nome del file HTML nella cartella `src/main/resources/templates`
    }

    @PostMapping(path = "/v1/monero/ecwid/getPayment")
    @ResponseBody
    public PaymentRequestEntity getPayment(@RequestParam(required = true) String id) throws Exception {
        Optional<PaymentRequestEntity> req = paymentRequestService.repository.findById(id);

        if (req.isPresent()) {
            PaymentRequestEntity paymentReq = req.get();

            paymentReq.setBlockchainHeight(paymentRequestService.wallet.getDaemonHeight());

            return paymentReq;
        }

        throw new Exception("Request " + id + " not found");
    }

}
