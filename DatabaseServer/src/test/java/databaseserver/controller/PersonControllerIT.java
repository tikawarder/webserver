package databaseserver.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import databaseserver.AbstractIntegrationTest;
import databaseserver.model.dto.PersonDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration test with real PostgreSQL (via Testcontainers).
 * Kafka is also real — PersonService saves to the Outbox table,
 * OutboxPublisher sends the event asynchronously.
 */
@Transactional
class PersonControllerIT extends AbstractIntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Smoke test: real PostgreSQL returns empty list on fresh DB.")
    void getAllPersons_shouldReturnEmptyList_whenNoData() throws Exception {
        mockMvc.perform(get("/api/persons")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)));
    }

    @Test
    @DisplayName("Save person to real PostgreSQL and verify it appears in the list.")
    void createAndListPerson_shouldWork() throws Exception {
        PersonDto newPerson = new PersonDto();
        newPerson.setName("John Doe");
        newPerson.setBirthDay(LocalDate.of(1990, 5, 20));
        newPerson.setCity("Debrecen");

        mockMvc.perform(post("/api/persons")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newPerson)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("John Doe")));

        mockMvc.perform(get("/api/persons")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].name", is("John Doe")));
    }
}
