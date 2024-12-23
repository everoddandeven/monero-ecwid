package monero.ecwid.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

@Component
public class DatabaseInitializer {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseInitializer.class);
    private static final String customDelimiter = "$$";

    private final JdbcTemplate jdbcTemplate;

    @Value("classpath:database/database.sql")
    private Resource databaseScript;

    @Value("classpath:database/events.sql")
    private Resource eventsScript;

    @Value("classpath:database/functions.sql")
    private Resource functionsScript;

    @Value("classpath:database/procedures.sql")
    private Resource proceduresScript;

    @Value("classpath:database/triggers.sql")
    private Resource triggersScript;

    @Value("classpath:database/views.sql")
    private Resource viewsScript;

    public DatabaseInitializer(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostConstruct
    public void initializeDatabase() {
        try {
            if (databaseExists()) {
                logger.info("Database monero_ecwid already exists");
                return;
            }

            executeSqlFiles(List.of(databaseScript, functionsScript, proceduresScript, viewsScript, triggersScript, eventsScript));
            logger.info("Successfully created monero_ecwid database");

        } catch (Exception e) {
            String msg = e.getMessage();

            if (e instanceof BadSqlGrammarException) {
                logger.error("Cannot initialize database: invalid syntax for schema");
            }
            else if (msg == null || msg.isEmpty()) {
                logger.error("An unknown error occured during database initilization", e);
            }
            else {
                logger.error("An error occured during database initilization: " + msg);
            }
        }
    }

    private boolean databaseExists() {
        try {
            String dbName = "monero_ecwid";
            
            String query = "SELECT SCHEMA_NAME FROM INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME = ?";
            String result = jdbcTemplate.queryForObject(query, new Object[]{dbName}, String.class);

            if (result == null) {
                return false;
            }

            String checkTableQuery = "SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = ? AND TABLE_NAME IN (?, ?)";
            Integer tableCount = jdbcTemplate.queryForObject(checkTableQuery, new Object[]{dbName, "payment_requests", "monero_transactions"}, Integer.class);
    
            if (tableCount == null) {
                return false;
            }
            else if (tableCount.equals(Integer.valueOf(2))) {
                logger.info("Database monero_ecwid exists.");
                return true;
            } else {
                logger.warn("Database is empty.");
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    private void executeStatement(Statement statement, String sql) throws Exception {
        statement.execute(sql.replace(customDelimiter, ""));
    }

    private void executeSqlFile(Resource resource) throws Exception {
        try (
            Connection connection = jdbcTemplate.getDataSource().getConnection();
            Statement statement = connection.createStatement();
            BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))
        ) {
            StringBuilder sqlBuilder = new StringBuilder();
            String line;

            boolean insideDelimiter = false;
            
            while ((line = reader.readLine()) != null) {
                line = line.trim();
    
                if (line.isEmpty() || line.startsWith("--") || line.startsWith("#")) {
                    continue;
                }
    
                if (line.toUpperCase().startsWith("DELIMITER")) {
                    if (insideDelimiter) {
                        insideDelimiter = false;

                        if (sqlBuilder.length() != 0) {
                            String sql = sqlBuilder.toString().trim();
                            executeStatement(statement, sql);
                            System.out.println("Eseguito blocco SQL:\n" + sql);
                            sqlBuilder.setLength(0);
                        }
                    }
                    else {
                        insideDelimiter = true;
                    }

                    continue;
                }

                sqlBuilder.append(line).append(" ");
    
                if (line.endsWith(";") && ! insideDelimiter) {
                    String sql = sqlBuilder.toString().trim();
                    executeStatement(statement, sql);    
                    sqlBuilder.setLength(0);
                }
            }
        
            if (sqlBuilder.length() != 0) {
                String sql = sqlBuilder.toString().trim();
                executeStatement(statement, sql);
                sqlBuilder.setLength(0);
            }
        } catch (Exception e) {
            throw new Exception("An error occured while executing sql file SQL: " + resource.getFilename(), e);
        }
    }
    
    private void executeSqlFiles(List<Resource> sqlFiles) throws Exception {
        for (Resource sqlFile : sqlFiles) {
            executeSqlFile(sqlFile);
        }
    }

}
