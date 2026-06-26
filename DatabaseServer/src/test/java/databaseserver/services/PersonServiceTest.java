package databaseserver.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import databaseserver.model.dto.PersonDto;
import databaseserver.model.entity.Person;
import databaseserver.repository.PersonRepository;
import databaseserver.repository.OutboxMessageRepository;
import databaseserver.services.mapper.PersonMapper;
import databaseserver.services.kafka.KafkaProducerService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersonServiceTest {

    @Mock
    private PersonRepository personRepository;

    @Mock
    private OutboxMessageRepository outboxMessageRepository;

    @Mock
    private PersonMapper personMapper;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private PersonService personService;

    @Test
    void getAllPersons_shouldReturnPageOfDtos() {
        // ARRANGE=given
        Person mockPerson = new Person();
        mockPerson.setId(1L);
        mockPerson.setName("Test Elek");
        Page<Person> personPage = new PageImpl<>(List.of(mockPerson));

        when(personRepository.findAll(any(Pageable.class))).thenReturn(personPage);

        PersonDto mockDto = new PersonDto();
        mockDto.setName("Test Elek");
        when(personMapper.toDto(mockPerson)).thenReturn(mockDto);

        // ACT=when
        Pageable pageable = PageRequest.of(0, 10);
        Page<PersonDto> result = personService.getAllPersons(pageable);

        // ASSERT=then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Test Elek", result.getContent().get(0).getName());

        verify(personRepository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("Create a person, set name and save to entity.")
    void createPerson_createDto_shouldSave() {
        // ARRANGE=given
        PersonDto inputDto = new PersonDto();
        inputDto.setName("New Person");

        Person personEntity = new Person();
        personEntity.setName("New Person");

        Person savedEntity = new Person();
        savedEntity.setId(123L);
        savedEntity.setName("New Person");

        PersonDto outputDto = new PersonDto();
        outputDto.setId(123L);
        outputDto.setName("New Person");

        when(personMapper.toEntity(inputDto)).thenReturn(personEntity);
        when(personRepository.save(personEntity)).thenReturn(savedEntity);
        when(personMapper.toDto(savedEntity)).thenReturn(outputDto);

        // ACT=when
        PersonDto result = personService.createPerson(inputDto);

        // ASSERT=then
        assertNotNull(result);
        assertEquals(123L, result.getId());
        assertEquals("New Person", result.getName());

        verify(personRepository, times(1)).save(personEntity);
    }
}