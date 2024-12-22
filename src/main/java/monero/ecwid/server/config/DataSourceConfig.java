package monero.ecwid.server.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.io.IOException;

@Configuration
public class DataSourceConfig {
    private static final Logger logger = LoggerFactory.getLogger(DataSourceConfig.class);


    public DataSourceConfig() {
    }

    private static ServerConfig getServerConfig() {
        try {
            return ServerConfigFileReader.read();
        }
        catch (IOException e) {
            logger.error("Config file not found");
            return new ServerConfig();
        }
    }

    public static String getDataSourceUrl() {
        ServerConfig config = getServerConfig();

        String host = config.dbHost;
        String port = config.dbPort.toString();
        return String.format("jdbc:mysql://%s:%s/monero_ecwid", host, port);
    }

    @Bean
    public DataSource dataSource() {
        ServerConfig config = getServerConfig();

        String username = config.dbUsername;
        String password = config.dbPassword;
        String url = getDataSourceUrl();

        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);

        return dataSource;
    }
}
