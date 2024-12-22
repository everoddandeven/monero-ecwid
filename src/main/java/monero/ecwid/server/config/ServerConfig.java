package monero.ecwid.server.config;

public class ServerConfig {
    
    public String dbHost = "localhost";
    public Integer dbPort = 3306;
    public String dbUsername = "monero_ecwid";
    public String dbPassword = "devpassword";
    public Long requiredConfirmations = Long.valueOf(10);

}
