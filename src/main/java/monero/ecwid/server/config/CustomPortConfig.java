package monero.ecwid.server.config;

import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.stereotype.Component;

@Component
public class CustomPortConfig implements WebServerFactoryCustomizer<ConfigurableWebServerFactory> {

    private final ServerConfig serverConfig = ServerConfig.getServerConfig();

    public CustomPortConfig() {
    }

    @Override
    public void customize(ConfigurableWebServerFactory factory) {
        factory.setPort(serverConfig.port);
        System.out.println("Server configurato per avviarsi sulla porta: " + serverConfig.port);
    }
}
