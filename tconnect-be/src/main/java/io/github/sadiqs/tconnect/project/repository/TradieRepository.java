package io.github.sadiqs.tconnect.project.repository;

import io.github.sadiqs.tconnect.project.model.Tradie;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TradieRepository extends CrudRepository<Tradie, UUID> {
    Optional<Tradie> findByUsername(String username);
}
