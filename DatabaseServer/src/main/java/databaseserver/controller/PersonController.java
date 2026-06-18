package databaseserver.controller;

import databaseserver.model.dto.PersonDto;
import databaseserver.services.PersonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/persons")
@CrossOrigin(origins = {"http://localhost:9080", "http://localhost:3000", "http://localhost:8080", "https://frontend-react-801953368913.us-east1.run.app", "https://dev.birotamas.hu"}, allowCredentials = "true")
@RequiredArgsConstructor
public class PersonController {

    private final PersonService personService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'GUEST')")
    public Page<PersonDto> getAllPersons(
            @PageableDefault(page = 0, size = 5, sort = "name") Pageable pageable,
            @AuthenticationPrincipal Jwt jwt) {
        return personService.getAllPersons(pageable);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<PersonDto> createPerson(@Valid @RequestBody PersonDto personDto) {
        return ResponseEntity.ok(personService.createPerson(personDto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePerson(@PathVariable Long id) {
        personService.deletePerson(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}