package io.github.sadiqs.tconnect.project.repository;

import io.github.sadiqs.tconnect.project.model.Bid;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Repository
public interface BidRepository extends CrudRepository<Bid, UUID> {
    List<Bid> findByTradie_Username(String username);
}
