package monero.ecwid.server.config;

import monero.daemon.model.MoneroNetworkType;

public class ServerConfig {

    private static ServerConfig config = null;
    
    public String dbHost = "localhost";
    public Integer dbPort = 3306;
    public String dbUsername = "monero_ecwid";
    public String dbPassword = "devpassword";
    public Long requiredConfirmations = Long.valueOf(10);
    public String walletAddress = "";
    public String walletViewKey = "";
    public String walletPassword = "";
    public String walletNetType = "testnet";
    public String walletServerUri = "";
    public String clientSecret = "";
    
    public String mailHost = "";
    public Integer mailPort = 587;
    public String mailUsername = "";
    public String mailPassword = "";

    public MoneroNetworkType getNetType() {
        if (walletNetType.toLowerCase().equals("mainnet")) {
            return MoneroNetworkType.MAINNET;
        }
        else if (walletNetType.toLowerCase().equals("testnet")) {
            return MoneroNetworkType.TESTNET;
        }
        else if (walletNetType.toLowerCase().equals("stagenet")) {
            return MoneroNetworkType.STAGENET;
        }

        return MoneroNetworkType.TESTNET;
    }

    public boolean validMailConfig() {
        return !mailHost.isEmpty() && !mailUsername.isEmpty() && !mailPassword.isEmpty() && mailPort.compareTo(Integer.valueOf(0)) > 0 ;
    }

    public static ServerConfig getServerConfig() {
        if (config == null) {
            try {
                config = ServerConfigFileReader.read();
            }
            catch (Exception e) {
                config = new ServerConfig();
            }
        }

        return config;
    }
}
