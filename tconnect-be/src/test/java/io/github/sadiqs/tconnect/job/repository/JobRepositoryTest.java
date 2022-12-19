package io.github.sadiqs.tconnect.job.repository;

import io.github.sadiqs.tconnect.job.model.JobStatus;
import io.github.sadiqs.tconnect.job.model.ProjectAllocationJob;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatList;

@DataJpaTest
class JobRepositoryTest {

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    JobRepository repository;

    @Test
    void findByNextExecutionTimeLessThanAndStatusIn_ShouldReturnOnlyEligibleJobs() {
        ProjectAllocationJob futureJob = new ProjectAllocationJob();
        futureJob.setProjectId(UUID.randomUUID());
        futureJob.setNextExecutionTime(Instant.now().plusSeconds(5));
        futureJob.setStatus(JobStatus.CREATED);
        futureJob.setInfo("futureJob");

        ProjectAllocationJob justMaturedJob = new ProjectAllocationJob();
        justMaturedJob.setProjectId(UUID.randomUUID());
        justMaturedJob.setNextExecutionTime(Instant.now().minusSeconds(5));
        justMaturedJob.setStatus(JobStatus.CREATED);
        justMaturedJob.setInfo("justMaturedJob");

        ProjectAllocationJob stillEnqueuedJob = new ProjectAllocationJob();
        stillEnqueuedJob.setProjectId(UUID.randomUUID());
        stillEnqueuedJob.setNextExecutionTime(Instant.now().plusSeconds(5));
        stillEnqueuedJob.setStatus(JobStatus.ENQUEUED);
        stillEnqueuedJob.setInfo("stillEnqueuedJob");

        ProjectAllocationJob stillExecutingButOverMaxAllowedExecutionTimeJob = new ProjectAllocationJob();
        stillExecutingButOverMaxAllowedExecutionTimeJob.setProjectId(UUID.randomUUID());
        stillExecutingButOverMaxAllowedExecutionTimeJob.setNextExecutionTime(Instant.now().minusSeconds(1));
        stillExecutingButOverMaxAllowedExecutionTimeJob.setStatus(JobStatus.ENQUEUED);
        stillExecutingButOverMaxAllowedExecutionTimeJob.setInfo("stillExecutingButOverMaxAllowedExecutionTimeJob");

        Stream.of(futureJob, justMaturedJob, stillEnqueuedJob, stillExecutingButOverMaxAllowedExecutionTimeJob)
                .forEach(entityManager::persistAndFlush);

        List<ProjectAllocationJob> result = repository.findByNextExecutionTimeLessThanAndStatusIn(Instant.now(), List.of(JobStatus.CREATED, JobStatus.ENQUEUED)).toList();

        assertThatList(result).containsExactly(justMaturedJob, stillExecutingButOverMaxAllowedExecutionTimeJob);
    }
}