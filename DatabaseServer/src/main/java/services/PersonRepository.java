package services;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import model.Person;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {
    // Nem kell implementálnod a save, findById, findAll, delete metódusokat,
    // a Spring Data JPA automatikusan tudja őket!
}