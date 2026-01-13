package databaseserver.services.mapper;

import databaseserver.model.entity.Person;
import databaseserver.model.dto.PersonDto;
import org.springframework.stereotype.Component;

@Component
public class PersonMapper {
    public PersonDto toDto(Person person) {
        if (person == null) return null;

        PersonDto dto = new PersonDto();
        dto.setId(person.getId());
        dto.setName(person.getName());
        dto.setBirthDay(person.getBirthDay());
        dto.setCity(person.getCity());
        return dto;
    }

    public Person toEntity(PersonDto dto) {
        if (dto == null) return null;

        Person person = new Person();
        person.setId(dto.getId());
        person.setName(dto.getName());
        person.setBirthDay(dto.getBirthDay());
        person.setCity(dto.getCity());
        return person;
    }
}