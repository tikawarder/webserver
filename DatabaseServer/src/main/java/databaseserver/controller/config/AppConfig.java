package databaseserver.controller.config;

import databaseserver.model.Account;
import databaseserver.model.Role;
import databaseserver.repository.AccountRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AppConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    CommandLineRunner initDatabase(AccountRepository accountRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (accountRepository.findByUsername("admin").isEmpty()) {
                Account admin = Account.builder()
                        .username("admin")
                        .password(passwordEncoder.encode("password"))
                        .role(Role.ADMIN)
                        .build();

                accountRepository.save(admin);
                System.out.println("Admin user has created.");
            }
        };
    }


}