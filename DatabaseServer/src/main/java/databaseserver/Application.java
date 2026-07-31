package databaseserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EntityScan({"databaseserver.model", "databaseserver.postgresql.model", "databaseserver.ai.observability"})
@EnableJpaRepositories({"databaseserver.repository", "databaseserver.postgresql.repository", "databaseserver.ai.observability"})
@EnableScheduling
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}