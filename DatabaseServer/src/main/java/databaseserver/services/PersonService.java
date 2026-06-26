package databaseserver.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import databaseserver.model.entity.Person;
import databaseserver.model.entity.OutboxMessage;
import databaseserver.model.dto.PersonDto;
import databaseserver.model.event.UserCreatedEvent;
import databaseserver.repository.PersonRepository;
import databaseserver.repository.OutboxMessageRepository;
import databaseserver.services.mapper.PersonMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PersonService {

    private final PersonRepository personRepository;
    private final OutboxMessageRepository outboxMessageRepository;
    private final PersonMapper personMapper;
    private final ObjectMapper objectMapper;
    private final AuthServiceClient authServiceClient;

    @Cacheable(cacheNames = "persons", key = "#pageable.pageNumber + '_' + #pageable.pageSize + '_' + #pageable.sort")
    @Transactional(readOnly = true)
    public Page<PersonDto> getAllPersons(Pageable pageable) {
        Page<Person> personsPage = personRepository.findAll(pageable);
        return personsPage.map(personMapper::toDto);
    }

    @CacheEvict(cacheNames = "persons", allEntries = true)
    @Transactional
    public void deletePerson(Long id) {
        personRepository.deleteById(id);
    }

    @CacheEvict(cacheNames = "persons", allEntries = true)
    @Transactional
    public PersonDto createPerson(PersonDto personDto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = null;
        if (auth instanceof JwtAuthenticationToken jwtAuth) {
            currentUsername = jwtAuth.getToken().getClaimAsString("preferred_username");
        }
        if (currentUsername == null && auth != null) {
            currentUsername = auth.getName();
        }

        if (!authServiceClient.validateUser(currentUsername)) {
            throw new AccessDeniedException("Active account verification failed in AuthService!");
        }

        Person entity = personMapper.toEntity(personDto);
        Person saved = personRepository.save(entity);

        // Build the event
        UserCreatedEvent event = UserCreatedEvent.builder()
                .id(saved.getId())
                .name(saved.getName())
                .birthDay(saved.getBirthDay())
                .city(saved.getCity())
                .message("New user joined via the modern React app!")
                .build();

        // Save to Outbox Table in the same transaction
        try {
            String payload = objectMapper.writeValueAsString(event);
            OutboxMessage outboxMessage = OutboxMessage.builder()
                    .eventType("USER_CREATED")
                    .payload(payload)
                    .processed(false)
                    .createdAt(LocalDateTime.now())
                    .build();
            outboxMessageRepository.save(outboxMessage);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize or save OutboxMessage", e);
        }

        return personMapper.toDto(saved);
    }
}