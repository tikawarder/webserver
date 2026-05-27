package databaseserver.postgresql.repository;

import databaseserver.postgresql.model.CustomerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<CustomerProfile, Long> {

    /**
     * Fetch profile WITH customer in a single query (JOIN FETCH).
     * Without JOIN FETCH: profile loads, then customer.getName() fires another query.
     */
    @Query("SELECT p FROM CustomerProfile p JOIN FETCH p.customer WHERE p.customer.id = :customerId")
    Optional<CustomerProfile> findByCustomerIdWithCustomer(@Param("customerId") Long customerId);

    Optional<CustomerProfile> findByCustomerId(Long customerId);
}
