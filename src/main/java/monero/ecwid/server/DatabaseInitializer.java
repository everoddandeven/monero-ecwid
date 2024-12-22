package monero.ecwid.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class DatabaseInitializer {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseInitializer.class);

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
            logger.error("Errore durante l'inizializzazione del database: " + e.getMessage());
        }
    }

    private boolean databaseExists() {
        try {
            String dbName = "monero_ecwid";
            
            String query = "SELECT SCHEMA_NAME FROM INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME = ?";
            String result = jdbcTemplate.queryForObject(query, new Object[]{dbName}, String.class);

            return result != null; // Il database esiste
        } catch (Exception e) {
            // Se si verifica un errore, presumiamo che il database non esista
            return false;
        }
    }

    private void executeSqlFiles(List<Resource> sqlFiles) throws Exception {
        for (Resource sqlFile : sqlFiles) {
            String sql = readResource(sqlFile);
            if (!sql.isBlank()) {
                jdbcTemplate.execute(sql);
                logger.info("Eseguito file SQL: " + sqlFile.getFilename());
            }
        }
    }

    private String readResource(Resource resource) throws Exception {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }
}
