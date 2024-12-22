package monero.ecwid.server.config;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
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
        String walletAddress = rawConfig.getOrDefault("wallet-address", "");
        String walletViewKey = rawConfig.getOrDefault("wallet-view-key", "");
        String walletServerUri = rawConfig.getOrDefault("wallet-server-uri", "");
        String walletPassword = rawConfig.getOrDefault("wallet-password", "");
        String walletNetType = rawConfig.getOrDefault("wallet-net-type", "");
        String clientSecret = rawConfig.getOrDefault("client-secret", "");

        Integer port = Integer.valueOf(dbPort);
        Long confirmations = Long.valueOf(reqConfirmations);

        config.dbHost = dbHost;
        config.dbPort = port;
        config.dbUsername = dbUsername;
        config.dbPassword = dbPassword;
        config.requiredConfirmations = confirmations;
        config.walletAddress = walletAddress;
        config.walletViewKey = walletViewKey;
        config.walletPassword = walletPassword;
        config.walletNetType = walletNetType;
        config.clientSecret = clientSecret;

        config.walletServerUri = walletServerUri;

        return config;
    }

    private static Map<String, String> parseConfigFile(String configFilePath) throws IOException {
        ensureConfigFileExists(configFilePath);
        
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

    private static void ensureConfigFileExists(String configFilePath) throws IOException {
        File configFile = new File(configFilePath);

        if (!configFile.exists()) {
            System.out.println("File di configurazione non trovato, creazione di un nuovo file con valori di default...");
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(configFile))) {
                writer.write("# Configurazione default per Monero ECWID Server\n");
                writer.write("db-host=localhost\n");
                writer.write("db-port=3306\n");
                writer.write("db-username=monero_ecwid\n");
                writer.write("db-password=\n");
                writer.write("required-confirmations=10\n");
                writer.write("wallet-address=\n");
                writer.write("wallet-view-key=\n");
                writer.write("wallet-server-uri=\n");
                writer.write("wallet-password=\n");
                writer.write("wallet-net-type=\n");
                writer.write("client-secret=\n");
            }
        }
    }

}
