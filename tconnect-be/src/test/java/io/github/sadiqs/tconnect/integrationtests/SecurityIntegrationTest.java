package io.github.sadiqs.tconnect.integrationtests;

import io.github.sadiqs.tconnect.job.repository.JobQueue;
import io.github.sadiqs.tconnect.project.model.Bid;
import io.github.sadiqs.tconnect.project.model.Project;
import io.github.sadiqs.tconnect.project.model.request.BidCreateRequest;
import io.github.sadiqs.tconnect.project.model.request.ProjectCreateRequest;
import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
@ActiveProfiles("integrationtest")
@EmbeddedKafka(partitions = 1, topics = JobQueue.JOB_QUEUE_NAME, bootstrapServersProperty = "spring.kafka.bootstrap-servers")
public class SecurityIntegrationTest {

    @Autowired
    TestRestTemplate restTemplate;
    private TestRestTemplate asCustomer;
    private TestRestTemplate asTradie;

    @PostConstruct
    void init() {
        asCustomer = restTemplate.withBasicAuth("customer1", "customer1");
        asTradie = restTemplate.withBasicAuth("tradie1", "tradie1");
    }

    @Test
    void customersShouldBeAbleToCreateProjects() {
        ProjectCreateRequest createRequest = new ProjectCreateRequest("proj", "description", 10, Instant.now().plusSeconds(10));

        ResponseEntity<Project> result = asCustomer
                .postForEntity("/projects", createRequest, Project.class);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void unAuthenticatedUsersShouldNotBeAbleToCreateProjects() {
        ProjectCreateRequest createRequest = new ProjectCreateRequest("proj", "description", 10, Instant.now().plusSeconds(10));

        ResponseEntity<Project> result = restTemplate.postForEntity("/projects", createRequest, Project.class);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void tradiesShouldNotBeAbleToCreateProjects() {
        ProjectCreateRequest createRequest = new ProjectCreateRequest("proj", "description", 10, Instant.now().plusSeconds(10));

        ResponseEntity<Project> result = asTradie.postForEntity("/projects", createRequest, Project.class);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void tradiesShouldBeAbleToPlaceBids() {
        var projectCreateRequest = new ProjectCreateRequest("proj", "description", 10, Instant.now().plusSeconds(10));
        Project project = createProject(projectCreateRequest);

        var bidCreateRequest = new BidCreateRequest(project.getId(), 10);
        ResponseEntity<Bid> bidResult = asTradie.postForEntity("/bids", bidCreateRequest, Bid.class);

        assertThat(bidResult.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void customersShouldNotBeAbleToPlaceBids() {
        var projectCreateRequest = new ProjectCreateRequest("proj", "description", 10, Instant.now().plusSeconds(10));
        Project project = createProject(projectCreateRequest);

        var bidCreateRequest = new BidCreateRequest(project.getId(), 10);
        ResponseEntity<Bid> bidResult = asCustomer.postForEntity("/bids", bidCreateRequest, Bid.class);

        assertThat(bidResult.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void unAuthenticatedUsersShouldNotBeAbleToPlaceBids() {
        var projectCreateRequest = new ProjectCreateRequest("proj", "description", 10, Instant.now().plusSeconds(10));
        Project project = createProject(projectCreateRequest);

        var bidCreateRequest = new BidCreateRequest(project.getId(), 10);
        ResponseEntity<Bid> bidResult = restTemplate.postForEntity("/bids", bidCreateRequest, Bid.class);

        assertThat(bidResult.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    private Project createProject(ProjectCreateRequest projectCreateRequest) {
        ResponseEntity<Project> result = asCustomer.postForEntity("/projects", projectCreateRequest, Project.class);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        return result.getBody();
    }

}
