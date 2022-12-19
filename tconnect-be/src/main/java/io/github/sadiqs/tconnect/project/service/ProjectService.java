package io.github.sadiqs.tconnect.project.service;

import io.github.sadiqs.tconnect.job.model.JobStatus;
import io.github.sadiqs.tconnect.job.model.ProjectAllocationJob;
import io.github.sadiqs.tconnect.job.repository.JobRepository;
import io.github.sadiqs.tconnect.project.model.Bid;
import io.github.sadiqs.tconnect.project.model.Customer;
import io.github.sadiqs.tconnect.project.model.Project;
import io.github.sadiqs.tconnect.project.model.Tradie;
import io.github.sadiqs.tconnect.project.model.request.BidCreateRequest;
import io.github.sadiqs.tconnect.project.model.request.ProjectCreateRequest;
import io.github.sadiqs.tconnect.project.repository.BidRepository;
import io.github.sadiqs.tconnect.project.repository.CustomerRepository;
import io.github.sadiqs.tconnect.project.repository.ProjectRepository;
import io.github.sadiqs.tconnect.project.repository.TradieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final BidRepository bidRepository;
    private final ProjectRepository projectRepository;
    private final TradieRepository tradieRepository;
    private final CustomerRepository customerRepository;

    private final JobRepository jobRepository;

    @Transactional
    public Project createProject(String customerUsername, ProjectCreateRequest request) {
        Customer customer = customerRepository.findByUsername(customerUsername).get();

        Project project = new Project();

        project.setTitle(request.title());
        project.setDescription(request.description());
        project.setExpectedHours(request.expectedHours());
        project.setCustomer(customer);
        project.setBiddingEndTime(request.biddingEndTime());
        project.setBids(List.of());

        Project savedProject = projectRepository.save(project);

        ProjectAllocationJob projectAllocationJob = new ProjectAllocationJob();

        projectAllocationJob.setProjectId(savedProject.getId());
        projectAllocationJob.setNextExecutionTime(savedProject.getBiddingEndTime());
        projectAllocationJob.setStatus(JobStatus.CREATED);

        jobRepository.save(projectAllocationJob);
        return savedProject;
    }

    public Bid placeBid(String tradieUsername, BidCreateRequest request) {
        Tradie tradie = tradieRepository.findByUsername(tradieUsername).get();
        Project project = projectRepository.findById(request.projectId()).get();

        Bid bid = new Bid();
        bid.setAmount(request.amount());
        bid.setProject(project);
        bid.setTradie(tradie);

        return bidRepository.save(bid);
    }

    public List<Project> getCustomerProjects(String username) {
        return projectRepository.findByCustomer_Username(username);
    }

    public Iterable<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    public List<Bid> getAllBids(String username) {
        return bidRepository.findByTradie_Username(username);
    }
}
