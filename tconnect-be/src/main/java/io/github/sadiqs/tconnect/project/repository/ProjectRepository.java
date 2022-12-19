package io.github.sadiqs.tconnect.project.repository;

import io.github.sadiqs.tconnect.project.model.Project;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProjectRepository extends CrudRepository<Project, UUID> {
    List<Project> findByCustomer_Username(String username);
}
