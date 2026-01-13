package databaseserver.services;

import databaseserver.model.entity.Person;
import databaseserver.model.dto.PersonDto;
import databaseserver.repository.PersonRepository;
import databaseserver.services.mapper.PersonMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor // Lombok
public class PersonService {

    private final PersonRepository personRepository;
    private final  PersonMapper personMapper; // constructor injection instead of @Autowired (new way)

    @Transactional(readOnly = true)
    public List<PersonDto> getAllPersons() {
        return personRepository.findAll()
                .stream()
                .map(personMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public PersonDto createPerson(PersonDto personDto) {
        Person entity = personMapper.toEntity(personDto);
        Person saved = personRepository.save(entity);
        return personMapper.toDto(saved);
    }
}