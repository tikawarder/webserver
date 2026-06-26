package reactiveservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactiveservice.model.Person;
import reactiveservice.model.PersonDto;
import reactiveservice.repository.PersonRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

// Previously used an in-memory ArrayList — this version delegates to PostgreSQL via R2DBC.
// The API shape (Mono/Flux) is unchanged; only the data source changed.
//
// Key difference from JPA service:
//   JPA findById() throws or returns Optional — caller blocks while DB responds.
//   Here findById() returns Mono<Person> — the DB call is scheduled on an I/O thread;
//   the event loop thread is free to handle other requests in the meantime.
@Service
@RequiredArgsConstructor
public class PersonService {

    private final PersonRepository repository;

    public Flux<PersonDto> findAll() {
        return repository.findAll().map(this::toDto);
    }

    public Mono<PersonDto> findById(Long id) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new NoSuchPersonException(id)))
                .map(this::toDto);
    }

    public Mono<PersonDto> save(PersonDto dto) {
        return repository.save(toEntity(dto)).map(this::toDto);
    }

    public Mono<Void> delete(Long id) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new NoSuchPersonException(id)))
                // flatMap because repository.delete() returns Mono<Void> — we chain, not transform
                .flatMap(repository::delete);
    }

    private PersonDto toDto(Person p) {
        return new PersonDto(p.getId(), p.getName(), p.getBirthDay(), p.getCity());
    }

    private Person toEntity(PersonDto dto) {
        // id=null on new entries — SERIAL in Postgres assigns it; R2DBC reads it back
        return new Person(dto.getId(), dto.getName(), dto.getBirthDay(), dto.getCity());
    }

    public static class NoSuchPersonException extends RuntimeException {
        public NoSuchPersonException(Long id) {
            super("Person not found: " + id);
        }
    }
}
