package monero.ecwid.server.config;

import java.util.Properties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class MailConfig {

    public MailConfig() {

    }

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        ServerConfig serverConfig = ServerConfig.getServerConfig();

        mailSender.setHost(serverConfig.mailHost);
        mailSender.setPort(serverConfig.mailPort);
        mailSender.setUsername(serverConfig.mailUsername);
        mailSender.setPassword(serverConfig.mailPassword);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", true);
        props.put("mail.smtp.starttls.enable", true);
        props.put("mail.smtp.starttls.required", true);

        return mailSender;
    }

}
