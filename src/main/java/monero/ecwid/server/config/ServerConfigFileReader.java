package monero.ecwid.server.config;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public abstract class ServerConfigFileReader {

    public static String getConfigFilePath() {
        return System.getProperty("config-file", "moneroecwid.conf");
    }

    public static ServerConfig read() throws IOException {
        return read(getConfigFilePath());
    }

    public static ServerConfig read(String configFilePath) throws IOException {
        Map<String, String> rawConfig = parseConfigFile(configFilePath);

        ServerConfig config = new ServerConfig();

        String dbHost = rawConfig.getOrDefault("db-host", "localhost");
        String dbPort = rawConfig.getOrDefault("db-port", "3306");
        String dbUsername = rawConfig.getOrDefault("db-username", "monero_ecwid");
        String dbPassword = rawConfig.getOrDefault("db-password", "");
        String reqConfirmations = rawConfig.getOrDefault("required-confirmations", "10");

        Integer port = Integer.valueOf(dbPort);
        Long confirmations = Long.valueOf(reqConfirmations);

        config.dbHost = dbHost;
        config.dbPort = port;
        config.dbUsername = dbUsername;
        config.dbPassword = dbPassword;
        config.requiredConfirmations = confirmations;

        return config;
    }

    private static Map<String, String> parseConfigFile(String configFilePath) throws IOException {
        Map<String, String> configMap = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(configFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty() || line.startsWith("#")) {
                    continue; // Ignora linee vuote o commenti
                }
                String[] keyValue = line.split("=", 2); // Dividi su "="
                if (keyValue.length == 2) {
                    configMap.put(keyValue[0].trim(), keyValue[1].trim());
                }
            }
        }

        return configMap;
    }

}
