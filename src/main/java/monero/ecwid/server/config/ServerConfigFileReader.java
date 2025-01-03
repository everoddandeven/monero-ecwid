package monero.ecwid.server.config;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Component
public abstract class ServerConfigFileReader {
    private static final Logger logger = LoggerFactory.getLogger(ServerConfigFileReader.class);

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
        String mailHost = rawConfig.getOrDefault("mail-host", "");
        String mailPort = rawConfig.getOrDefault("mail-port", "");
        String mailUsername = rawConfig.getOrDefault("mail-username", "");
        String mailPassword = rawConfig.getOrDefault("mail-password", "");
        String serverPort = rawConfig.getOrDefault("port", "8080");
        String walletRestoreHeight = rawConfig.getOrDefault("wallet-restore-height", "0");

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
        config.walletServerUri = walletServerUri;
        config.walletRestoreHeight = Long.valueOf(walletRestoreHeight);

        config.clientSecret = clientSecret;

        config.mailHost = mailHost;
        config.mailPort = Integer.valueOf(mailPort);
        config.mailUsername = mailUsername;
        config.mailPassword = mailPassword;
        
        config.port = Integer.valueOf(serverPort);

        return config;
    }

    private static Map<String, String> parseConfigFile(String configFilePath) throws IOException {
        ensureConfigFileExists(configFilePath);
        
        Map<String, String> configMap = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(configFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty() || line.startsWith("#")) {
                    continue;
                }
                String[] keyValue = line.split("=", 2);
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
            logger.info("File di configurazione non trovato, creazione di un nuovo file con valori di default...");

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(configFile))) {
                writer.write("# Default Monero Ecwid configuration\n");
                writer.write("db-host=localhost\n");
                writer.write("db-port=3306\n");
                writer.write("db-username=monero_ecwid\n");
                writer.write("db-password=devpassword\n");
                writer.write("required-confirmations=10\n");
                writer.write("wallet-address=\n");
                writer.write("wallet-view-key=\n");
                writer.write("wallet-server-uri=http://node2.monerodevs.org:28089\n");
                writer.write("wallet-password=supersecretpassword123\n");
                writer.write("wallet-net-type=testnet\n");
                writer.write("client-secret=\n");
                writer.write("mail-host=\n");
                writer.write("mail-port=\n");
                writer.write("mail-username=\n");
                writer.write("mail-password=\n");
            }
        }
    }

}
