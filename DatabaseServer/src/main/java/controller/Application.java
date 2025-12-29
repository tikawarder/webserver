package controller;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan("model") // Megmondjuk, hol vannak az @Entity osztályok (Person)
@EnableJpaRepositories("services") // Hol vannak a Repository interfészek
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}