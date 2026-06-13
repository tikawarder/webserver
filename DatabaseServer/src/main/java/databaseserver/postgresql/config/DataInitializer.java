package databaseserver.postgresql.config;

import databaseserver.postgresql.service.PostgresDemoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * Automatically seeds the demo database on application startup.
 *
 * ApplicationRunner runs AFTER the Spring context is fully initialized,
 * which means JPA and Hibernate are ready and the schema has been created.
 *
 * This is preferable to @PostConstruct for database operations.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final PostgresDemoService demoService;
    private final databaseserver.postgresql.service.RelationshipDemoService relationService;

    @Override
    public void run(ApplicationArguments args) {
        log.info("=== PostgreSQL Demo: Seeding sample data ===");
        demoService.seedData();
        relationService.seedRelationshipData();
        log.info("=== PostgreSQL Demo: Ready. Call /api/demo/* endpoints ===");
    }
}
