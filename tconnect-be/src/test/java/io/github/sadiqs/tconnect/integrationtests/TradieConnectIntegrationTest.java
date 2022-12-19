package io.github.sadiqs.tconnect.integrationtests;

import io.github.sadiqs.tconnect.job.model.JobStatus;
import io.github.sadiqs.tconnect.job.model.ProjectAllocationJob;
import io.github.sadiqs.tconnect.job.repository.JobRepository;
import io.github.sadiqs.tconnect.job.repository.JobQueue;
import io.github.sadiqs.tconnect.project.model.Bid;
import io.github.sadiqs.tconnect.project.model.Project;
import io.github.sadiqs.tconnect.project.model.request.BidCreateRequest;
import io.github.sadiqs.tconnect.project.model.request.ProjectCreateRequest;
import io.github.sadiqs.tconnect.project.repository.BidRepository;
import io.github.sadiqs.tconnect.project.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
@EmbeddedKafka(partitions = 1, topics = JobQueue.JOB_QUEUE_NAME, bootstrapServersProperty = "spring.kafka.bootstrap-servers")
@ActiveProfiles("integrationtest")
public class TradieConnectIntegrationTest {

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    EmbeddedKafkaBroker embeddedKafka;

    @Autowired
    ProjectRepository projectRepository;

    @Autowired
    BidRepository bidRepository;

    @Autowired
    JobRepository jobRepository;

    @BeforeEach
    void setUp() {
    }

    @Test
    void shouldBeAbleToCreateProjects() {
        ProjectCreateRequest createRequest = new ProjectCreateRequest("proj", "description", 10, Instant.now().plusSeconds(10));

        Project project = createProject(createRequest);
        assertThat(project != null);
        assertThat(project.getId() != null);
        assertThat(project.getTitle()).isEqualTo("proj");

        assertThat(projectRepository.findById(project.getId())).isNotEmpty();
    }

    @Test
    void shouldBeAbleToPlaceBids() {
        var projectCreateRequest = new ProjectCreateRequest("proj", "description", 10, Instant.now().plusSeconds(10));

        Project project = createProject(projectCreateRequest);

        Bid bid = placeBid(project, 10);
        assertThat(bidRepository.findById(bid.getId())).isNotEmpty();
    }

    @Test
    void shouldMarkProjectAsCompletedIfNoBids() throws InterruptedException {
        var projectCreateRequest = new ProjectCreateRequest("proj2", "description", 10, Instant.now());

        Project project = createProject(projectCreateRequest);

        TimeUnit.MILLISECONDS.sleep(500);
        Optional<ProjectAllocationJob> optionalJob = jobRepository.findById(project.getId());

        assertThat(optionalJob).hasValueSatisfying(job -> {
            assertThat(job.getStatus()).isEqualTo(JobStatus.COMPLETED);
            assertThat(job.getInfo()).contains("No bid");
        });
    }

    @Test
    void shouldSelectMaximumBidAndMarkProjectAsCompleted() throws InterruptedException {
        var projectCreateRequest = new ProjectCreateRequest("proj4", "description", 10, Instant.now().plusMillis(100));

        Project project = createProject(projectCreateRequest);

        placeBid(project, 20);
        Bid expectedWinningBid = placeBid(project, 30);
        placeBid(project, 10);

        TimeUnit.MILLISECONDS.sleep(500);

        Optional<ProjectAllocationJob> optionalJob = jobRepository.findById(project.getId());

        assertThat(optionalJob).hasValueSatisfying(job -> {
            assertThat(job.getStatus()).isEqualTo(JobStatus.COMPLETED);
            assertThat(job.getInfo()).contains("winning bid").contains(expectedWinningBid.getId().toString());
        });
    }

    private Project createProject(ProjectCreateRequest projectCreateRequest) {
        ResponseEntity<Project> result = restTemplate.withBasicAuth("customer1", "customer1")
                .postForEntity("/projects", projectCreateRequest, Project.class);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        return result.getBody();
    }

    private Bid placeBid(Project project, int amount) {
        var bidCreateRequest = new BidCreateRequest(project.getId(), amount);
        ResponseEntity<Bid> bidResult = restTemplate.withBasicAuth("tradie1", "tradie1")
                .postForEntity("/bids", bidCreateRequest, Bid.class);

        assertThat(bidResult.getStatusCode()).isEqualTo(HttpStatus.OK);
        return bidResult.getBody();
    }

}
