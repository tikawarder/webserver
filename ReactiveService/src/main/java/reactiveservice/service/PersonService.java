package reactiveservice.service;

import org.springframework.stereotype.Service;
import reactiveservice.model.PersonDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

// In-memory store — no database yet.
// The goal here is the reactive API shape, not persistence.
// R2DBC (reactive database driver) would be the next step in a real project.
@Service
public class PersonService {

    private final List<PersonDto> store = new ArrayList<>(List.of(
            new PersonDto(1L, "Tamás", LocalDate.of(1990, 5, 12), "Budapest"),
            new PersonDto(2L, "Anna",  LocalDate.of(1995, 3, 22), "Debrecen"),
            new PersonDto(3L, "Béla",  LocalDate.of(1988, 11, 7), "Pécs")
    ));

    private final AtomicLong idSequence = new AtomicLong(4);

    public Flux<PersonDto> findAll() {
        return Flux.fromIterable(store);
    }

    public Mono<PersonDto> findById(Long id) {
        return Flux.fromIterable(store)
                .filter(p -> p.getId().equals(id))
                .next()                              // Flux → Mono (first match or empty)
                .switchIfEmpty(Mono.error(new NoSuchPersonException(id)));
    }

    public Mono<PersonDto> save(PersonDto dto) {
        dto.setId(idSequence.getAndIncrement());
        store.add(dto);
        return Mono.just(dto);
    }

    public Mono<Void> delete(Long id) {
        return findById(id)
                .doOnNext(store::remove)
                .then();                             // discard the value, return Mono<Void>
    }

    public static class NoSuchPersonException extends RuntimeException {
        public NoSuchPersonException(Long id) {
            super("Person not found: " + id);
        }
    }
}
