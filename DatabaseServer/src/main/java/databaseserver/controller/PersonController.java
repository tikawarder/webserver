package databaseserver.controller;

import databaseserver.model.dto.PersonDto;
import databaseserver.services.PersonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/persons")
@CrossOrigin(origins = {"http://localhost:9080", "http://localhost:3000", "http://localhost:8080", "https://frontend-react-801953368913.us-east1.run.app", "https://dev.birotamas.hu"}, allowCredentials = "true")
@RequiredArgsConstructor
public class PersonController {

    private final PersonService personService;

    @GetMapping
    public Page<PersonDto> getAllPersons(@PageableDefault(page = 0, size = 5, sort = "name") Pageable pageable) {
        return personService.getAllPersons(pageable);
    }

    @PostMapping
    public ResponseEntity<PersonDto> createPerson(@Valid @RequestBody PersonDto personDto) {
        return ResponseEntity.ok(personService.createPerson(personDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePerson(@PathVariable Long id) {
        personService.deletePerson(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}