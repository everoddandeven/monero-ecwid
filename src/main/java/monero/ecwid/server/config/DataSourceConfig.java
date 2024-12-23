package monero.ecwid.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {


    public DataSourceConfig() {
    }

    public static String getDataSourceUrl() {
        ServerConfig config = ServerConfig.getServerConfig();

        String host = config.dbHost;
        String port = config.dbPort.toString();
        return String.format("jdbc:mysql://%s:%s/monero_ecwid?createDatabaseIfNotExist=true", host, port);
    }

    @Bean
    public DataSource dataSource() {
        ServerConfig config = ServerConfig.getServerConfig();

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
