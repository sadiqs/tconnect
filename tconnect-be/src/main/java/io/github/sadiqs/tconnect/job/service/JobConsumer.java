package io.github.sadiqs.tconnect.job.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.sadiqs.tconnect.job.model.JobStatus;
import io.github.sadiqs.tconnect.job.model.ProjectAllocationJob;
import io.github.sadiqs.tconnect.job.repository.JobQueue;
import io.github.sadiqs.tconnect.job.repository.JobRepository;
import io.github.sadiqs.tconnect.project.model.Bid;
import io.github.sadiqs.tconnect.project.model.Project;
import io.github.sadiqs.tconnect.project.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.Optional;

@RequiredArgsConstructor
@Component
@Slf4j
public class JobConsumer {

    private final ProjectRepository repository;
    private final ObjectMapper objectMapper;

    private final JobRepository jobRepository;

    @KafkaListener(topics = JobQueue.JOB_QUEUE_NAME)
    @Transactional
    public ProjectAllocationJob processJob(String message) throws JsonProcessingException {
        ProjectAllocationJob job = parseJobMessage(message);
        Optional<Project> projectOptional = repository.findById(job.getProjectId());

        projectOptional.ifPresentOrElse(project -> {

            Optional<Bid> optionalBid = project.getBids().stream().max(Comparator.comparingInt(bid -> bid.getAmount()));
            optionalBid.ifPresentOrElse(bid -> {
                String msg = String.format("The winning bid for project %s is %s by %s [%s] ", bid.getProject().getId(), bid.getId(), bid.getTradie().getName(), bid.getTradie().getId());
                log.info(msg);
                job.setStatus(JobStatus.COMPLETED);
                job.setInfo(msg);
            }, () -> {
                String msg = String.format("No bids found for project: %s", project.getId());
                log.info(msg);
                job.setStatus(JobStatus.COMPLETED);
                job.setInfo(msg);
            });

        }, () -> {
            String msg = String.format("There is no project with ID %s. Ignoring job", job.getProjectId());
            log.warn(msg);
            job.setStatus(JobStatus.FAILED);
            job.setInfo(msg);
        });

        return job;
    }

    private ProjectAllocationJob parseJobMessage(String message) throws JsonProcessingException {
        try {
            ProjectAllocationJob job = objectMapper.readValue(message, ProjectAllocationJob.class);
            return jobRepository.findById(job.getProjectId()).get();
        } catch (Exception e) {
            log.error("Error processing allocation job [" + message + "]", e);
            throw e;
        }
    }
}
