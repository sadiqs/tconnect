package io.github.sadiqs.tconnect.job.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Entity
@Data
public class ProjectAllocationJob {
    @Id
    private UUID projectId;
    private Instant nextExecutionTime;
    private JobStatus status;
    private String info;
}
