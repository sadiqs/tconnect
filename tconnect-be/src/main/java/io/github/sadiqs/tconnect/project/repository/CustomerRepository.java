package io.github.sadiqs.tconnect.project.repository;

import io.github.sadiqs.tconnect.project.model.Customer;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomerRepository extends CrudRepository<Customer, UUID> {
    Optional<Customer> findByUsername(String username);
}
