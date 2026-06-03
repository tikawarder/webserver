package authservice.contract;

import authservice.model.Role;
import authservice.model.entity.Account;
import authservice.repository.AccountRepository;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public abstract class ContractBase {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @BeforeEach
    void setup() {
        RestAssuredMockMvc.mockMvc(mockMvc);
        accountRepository.deleteAll();
        // "testuser" létezik → validateExistingUser contract teljesül
        // "unknown" nem létezik → validateNonExistingUser contract teljesül
        accountRepository.save(Account.builder()
                .username("testuser")
                .password(passwordEncoder.encode("password"))
                .role(Role.USER)
                .build());
    }
}
