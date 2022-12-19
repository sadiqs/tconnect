package io.github.sadiqs.tconnect.job.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.github.sadiqs.tconnect.job.model.JobStatus;
import io.github.sadiqs.tconnect.job.model.ProjectAllocationJob;
import io.github.sadiqs.tconnect.job.repository.JobRepository;
import io.github.sadiqs.tconnect.project.model.Bid;
import io.github.sadiqs.tconnect.project.model.Project;
import io.github.sadiqs.tconnect.project.model.Tradie;
import io.github.sadiqs.tconnect.project.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class JobConsumerTest {

    @Mock
    ProjectRepository repository;

    @Mock
    JobRepository jobRepository;

    ObjectMapper objectMapper;

    JobConsumer jobConsumer;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        jobConsumer = new JobConsumer(repository, objectMapper, jobRepository);
    }

    @Test
    void itShouldParseAndProcessJobEvenIfNoBids() throws JsonProcessingException {
        UUID projectId = UUID.randomUUID();

        Project project = new Project();
        Mockito.when(repository.findById(projectId)).thenReturn(Optional.of(project));

        String message = createJobMessage(projectId);
        ProjectAllocationJob processedJob = jobConsumer.processJob(message);

        Mockito.verify(repository).findById(ArgumentMatchers.eq(projectId));
        assertThat(processedJob.getStatus()).isEqualTo(JobStatus.COMPLETED);
        assertThat(processedJob.getInfo()).contains("No bids");
    }

    @Test
    void itShouldFindMaximumBidAmongAllBids() throws JsonProcessingException {
        UUID projectId = UUID.randomUUID();

        List<Bid> bids = setUpProjectWithBids(projectId);

        String message = createJobMessage(projectId);
        ProjectAllocationJob processedJob = jobConsumer.processJob(message);

        assertThat(processedJob.getStatus()).isEqualTo(JobStatus.COMPLETED);
        assertThat(processedJob.getInfo()).contains("winning bid");
        assertThat(processedJob.getInfo()).contains(bids.get(1).getId().toString());
    }

    @Test
    void itShouldFailIfTheProjectIsNotFound() throws JsonProcessingException {
        UUID projectId = UUID.randomUUID();
        Mockito.when(repository.findById(projectId)).thenReturn(Optional.empty());

        String message = createJobMessage(projectId);
        ProjectAllocationJob processedJob = jobConsumer.processJob(message);

        assertThat(processedJob.getStatus()).isEqualTo(JobStatus.FAILED);
        assertThat(processedJob.getInfo()).contains("no project").contains("Ignoring");
    }

    private String createJobMessage(UUID projectId) throws JsonProcessingException {
        ProjectAllocationJob job = new ProjectAllocationJob();
        job.setProjectId(projectId);
        job.setStatus(JobStatus.ENQUEUED);
        job.setNextExecutionTime(Instant.now().plusSeconds(3));

        String message = objectMapper.writeValueAsString(job);
        Mockito.when(jobRepository.findById(projectId)).thenReturn(Optional.of(job));
        return message;
    }

    private List<Bid> setUpProjectWithBids(UUID projectId) {
        Project project = new Project();
        project.setId(projectId);

        Bid bid1 = new Bid();
        bid1.setId(UUID.randomUUID());
        bid1.setAmount(20);
        bid1.setTradie(new Tradie());
        bid1.setProject(project);

        Bid bid2 = new Bid();
        bid2.setId(UUID.randomUUID());
        bid2.setAmount(30);
        bid2.setTradie(new Tradie());
        bid2.setProject(project);

        Bid bid3 = new Bid();
        bid3.setId(UUID.randomUUID());
        bid3.setAmount(10);
        bid3.setTradie(new Tradie());
        bid3.setProject(project);

        List<Bid> bids = List.of(bid1, bid2, bid3);
        project.setBids(bids);

        Mockito.when(repository.findById(projectId)).thenReturn(Optional.of(project));
        return bids;
    }
}