package io.github.sadiqs.tconnect.job.service;

import io.github.sadiqs.tconnect.job.config.JobConfig;
import io.github.sadiqs.tconnect.job.model.ProjectAllocationJob;
import io.github.sadiqs.tconnect.job.repository.JobQueue;
import io.github.sadiqs.tconnect.job.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Stream;

import static io.github.sadiqs.tconnect.job.model.JobStatus.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class JobPollingService {

    private final JobRepository jobRepository;

    private final JobQueue jobQueue;

    private final JobConfig jobConfig;

    @Scheduled(fixedRateString = "${job.poll.interval}")
    @Transactional
    public void pollProjectAllocationJobs() {
        Stream<ProjectAllocationJob> eligibleJobs = jobRepository.findByNextExecutionTimeLessThanAndStatusIn(Instant.now(), List.of(CREATED, ENQUEUED));
        eligibleJobs.forEach(this::enqueue);
    }

    private void enqueue(ProjectAllocationJob job) {
        try {
            log.info("JOBs: " + jobConfig.getMaxJobExecutionTime());
            job.setStatus(ENQUEUED);
            job.setNextExecutionTime(Instant.now().plusMillis(jobConfig.getMaxJobExecutionTime().toMillis()));
            job.setInfo(null); // clear earlier info. Ideally we should have a new table with a new row for each execution.
            jobQueue.enqueue(job);
        } catch (Exception e) {
            log.error("Failed to enqueue project allocation job for project " + job.getProjectId(), e);
            job.setStatus(FAILED);
            job.setInfo(e.getMessage());
        }
    }
}
