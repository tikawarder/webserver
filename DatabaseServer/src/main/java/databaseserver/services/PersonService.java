package databaseserver.services;

import databaseserver.model.entity.Person;
import databaseserver.model.dto.PersonDto;
import databaseserver.repository.PersonRepository;
import databaseserver.services.mapper.PersonMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor // Lombok
public class PersonService {

    private final PersonRepository personRepository;
    private final  PersonMapper personMapper; // constructor injection instead of @Autowired (new way)

    @Transactional(readOnly = true)
    public Page<PersonDto> getAllPersons(Pageable pageable) {
        Page<Person> personsPage = personRepository.findAll(pageable);
        return personsPage.map(personMapper::toDto);
    }

    @Transactional
    public PersonDto createPerson(PersonDto personDto) {
        Person entity = personMapper.toEntity(personDto);
        Person saved = personRepository.save(entity);
        return personMapper.toDto(saved);
    }
}