package monero.ecwid.server.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import monero.ecwid.server.config.ServerConfig;
import monero.ecwid.server.repository.PaymentRequestEntity;

@Service
public class MailService {

    public static class ReceiptMessage extends SimpleMailMessage {
        private PaymentRequestEntity request;

        public ReceiptMessage(PaymentRequestEntity request) {
            this.request = request;
            init();
        }

        private void init() {
            setSubject();
            setText();
            setTo(request.getCustomerMail());
            setFrom(ServerConfig.getServerConfig().mailUsername);
        }

        private void setSubject() {
            setSubject("Monero Payment Receipt " + getTxId());
        }

        private void setText() {

        }

        public String getTxId() {
            return request.getTxId();
        }
    }

    private final JavaMailSender sender;

    public MailService(JavaMailSender sender) {
        this.sender = sender;
    }
    
    public boolean isEnabled() {
        return ServerConfig.getServerConfig().validMailConfig();
    }
    
    public void sendReceipt(PaymentRequestEntity request) {
        if (!isEnabled()) {
            throw new Error("Cannot send receipt, mail is not configured");
        }
        
        ReceiptMessage msg = new ReceiptMessage(request);

        sender.send(msg);
    }

}
