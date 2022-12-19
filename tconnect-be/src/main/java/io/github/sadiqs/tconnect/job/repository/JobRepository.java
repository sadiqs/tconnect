package io.github.sadiqs.tconnect.job.repository;

import io.github.sadiqs.tconnect.job.model.JobStatus;
import io.github.sadiqs.tconnect.job.model.ProjectAllocationJob;
import org.springframework.data.repository.CrudRepository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

public interface JobRepository extends CrudRepository<ProjectAllocationJob, UUID> {
    Stream<ProjectAllocationJob> findByNextExecutionTimeLessThanAndStatusIn(Instant currentTime, List<JobStatus> statuses);
}
