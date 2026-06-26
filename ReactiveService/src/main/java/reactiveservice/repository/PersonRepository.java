package reactiveservice.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactiveservice.model.Person;

// ReactiveCrudRepository gives us Mono/Flux-returning methods for free:
//   findAll()       → Flux<Person>
//   findById(Long)  → Mono<Person>
//   save(Person)    → Mono<Person>   (INSERT if id==null, UPDATE if id!=null)
//   delete(Person)  → Mono<Void>
//
// Compare with JPA: JpaRepository<Person, Long> returns Page/List (blocking).
// Here every call is non-blocking by design.
public interface PersonRepository extends ReactiveCrudRepository<Person, Long> {
}
