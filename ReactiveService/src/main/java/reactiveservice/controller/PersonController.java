package reactiveservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactiveservice.model.PersonDto;
import reactiveservice.service.PersonService;
import reactiveservice.service.PersonService.NoSuchPersonException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

// Compare with DatabaseServer PersonController — same CRUD, different return types:
//
//   MVC (DatabaseServer)              WebFlux (here)
//   Page<PersonDto>               →   Flux<PersonDto>
//   ResponseEntity<PersonDto>     →   Mono<ResponseEntity<PersonDto>>
//   ResponseEntity<Void>          →   Mono<ResponseEntity<Void>>
//
// Everything is wrapped in Mono/Flux. Spring subscribes automatically on each request.
@RestController
@RequestMapping("/api/persons")
@RequiredArgsConstructor
public class PersonController {

    private final PersonService personService;

    @GetMapping
    public Flux<PersonDto> getAll() {
        return personService.findAll();
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<PersonDto>> getById(@PathVariable Long id) {
        return personService.findById(id)
                .map(ResponseEntity::ok)
                .onErrorReturn(NoSuchPersonException.class, ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<PersonDto> create(@RequestBody PersonDto dto) {
        return personService.save(dto);
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable Long id) {
        return personService.delete(id)
                .then(Mono.just(ResponseEntity.<Void>noContent().build()))
                .onErrorReturn(NoSuchPersonException.class, ResponseEntity.notFound().build());
    }
}
