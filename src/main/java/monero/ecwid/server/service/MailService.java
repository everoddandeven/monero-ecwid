package monero.ecwid.server.service;

import java.math.BigDecimal;
import java.text.DateFormat;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import monero.ecwid.server.config.ServerConfig;
import monero.ecwid.server.repository.PaymentRequestEntity;

@Service
public class MailService {

    private static abstract class Template {
        
        private static final String Receipt = """
                <h4>Monero Payment Receipt</h4>

                <br>
                <strong>Order ID:</strong> {txId}
                <br>
                <strong>Created At:</strong> {createdAt}
                <br>
                <strong>Total USD:</strong> {totalUSD}
                <br>
                <strong>Paid XMR:</strong> {amountPaid}

                <br>
                <strong>Successfully paid</strong>
                """;
        
        private static final String Cancelled = """
                <h4>Monero Payment Cancelled</h4>

                <br>
                <strong>Order ID:</strong> {txId}
                <br>
                <strong>Created At:</strong> {createdAt}
                <br>
                <strong>Total USD:</strong> {totalUSD}
                <br>
                <strong>Order cancelled</strong>
                """;
        
        private static final String Invoice = """
                <h4>Monero Payment Invoice</h4>

                <br>
                <strong>Order ID:</strong> {txId}
                <br>
                <strong>Created At:</strong> {createdAt}
                <br>
                <strong>Total USD:</strong> {totalUSD}
                <br>
                <strong>Total XMR:</strong> {amountToPay}
                <br>
                <strong>Payment Address:</strong> {address}
                <br>
                <strong>Invoince will expire within 15 minutes</strong>
                """;
        
        private static String replaceParams(String template, PaymentRequestEntity request) {
            BigDecimal piconeroDivider = BigDecimal.valueOf(1000000000000l);
            BigDecimal toPay = BigDecimal.valueOf(request.getAmountToPay().longValue()).divide(piconeroDivider);
            String amountToPay = toPay.toString();
            String createdAt = DateFormat.getInstance().format(request.getCreatedAt());
            String address = request.getAddress();
            String totalUSD = request.getAmountUsd().toString();
            String amountPaid = BigDecimal.valueOf(request.getAmountDeposited().longValue()).divide(piconeroDivider).toString();

            return template
                .replace("{txId}", request.getTxId())
                .replace("{amountToPay}", amountToPay)
                .replace("{amountPaid}", amountPaid)
                .replace("{totalUSD}", totalUSD)
                .replace("{createdAt}", createdAt)
                .replace("{address}", address);
        }

        public static String createReceiptTemplate(PaymentRequestEntity request) {
            String template = new String(Receipt);

            return replaceParams(template, request);
        }

        public static String createCancelledTemplate(PaymentRequestEntity request) {
            String template = new String(Cancelled);

            return replaceParams(template, request);
        }

        public static String createInvoiceTemplate(PaymentRequestEntity request) {
            String template = new String(Invoice);

            return replaceParams(template, request);
        }
    }

    private final JavaMailSender sender;

    public MailService(JavaMailSender sender) {
        this.sender = sender;
    }
    
    public boolean isEnabled() {
        return ServerConfig.getServerConfig().validMailConfig();
    }

    private MimeMessage createMessage(String to) throws MessagingException {
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");
        String from = ServerConfig.getServerConfig().mailUsername;

        helper.setFrom(from);
        helper.setTo(to);

        return message;
    }

    private MimeMessage createInvoiceMessage(PaymentRequestEntity request) throws MessagingException {
        MimeMessage message = createMessage(request.getCustomerMail());
        MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");
        String txId = request.getTxId();

        helper.setSubject("Monero Payment Invoice " + txId);        
        helper.setText(Template.createInvoiceTemplate(request), true); // Il secondo parametro `true` abilita il formato HTML

        return message;
    }

    private MimeMessage createReceiptMessage(PaymentRequestEntity request) throws MessagingException {
        MimeMessage message = createMessage(request.getCustomerMail());
        MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");
        String txId = request.getTxId();

        helper.setSubject("Monero Payment Receipt " + txId);        
        helper.setText(Template.createReceiptTemplate(request), true); // Il secondo parametro `true` abilita il formato HTML

        return message;
    }

    private MimeMessage createCancelledReceiptMessage(PaymentRequestEntity request) throws MessagingException {
        MimeMessage message = createMessage(request.getCustomerMail());
        MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");
        String txId = request.getTxId();

        helper.setSubject("Monero Payment Cancelled " + txId);        
        helper.setText(Template.createCancelledTemplate(request), true); // Il secondo parametro `true` abilita il formato HTML

        return message;
    }

    public void sendReceipt(PaymentRequestEntity request) throws MessagingException {
        if (!isEnabled()) {
            throw new RuntimeException("Cannot send receipt, mail is not configured");
        }
        
        sender.send(createReceiptMessage(request));
    }

    public void sendCancelledReceipt(PaymentRequestEntity request) throws MessagingException {
        if (!isEnabled()) {
            throw new RuntimeException("Cannot send receipt, mail is not configured");
        }
        
        sender.send(createCancelledReceiptMessage(request));
    }

    public void sendInvoince(PaymentRequestEntity request) throws MessagingException {
        if (!isEnabled()) {
            throw new RuntimeException("Cannot send receipt, mail is not configured");
        }
        
        sender.send(createInvoiceMessage(request));
    }

}
