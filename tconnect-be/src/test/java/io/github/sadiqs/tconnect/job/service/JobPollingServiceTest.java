package io.github.sadiqs.tconnect.job.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.github.sadiqs.tconnect.job.config.JobConfig;
import io.github.sadiqs.tconnect.job.model.JobStatus;
import io.github.sadiqs.tconnect.job.model.ProjectAllocationJob;
import io.github.sadiqs.tconnect.job.repository.JobQueue;
import io.github.sadiqs.tconnect.job.repository.JobRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.datasource.lookup.DataSourceLookupFailureException;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;

@ExtendWith(MockitoExtension.class)
class JobPollingServiceTest {
    @Mock
    JobRepository jobRepository;
    @Mock
    JobQueue jobQueue;
    @Spy
    JobConfig jobConfig;
    @InjectMocks
    JobPollingService jobPollingService;

    @Captor
    ArgumentCaptor<ProjectAllocationJob> jobCaptor;

    @BeforeEach
    void setUp() {
        Mockito.reset(jobRepository, jobQueue);
        jobConfig.setMaxJobExecutionTime(Duration.of(2, ChronoUnit.SECONDS));
    }

    @Test
    void pollingServiceShouldPickEligibleJobsAndUpdateStatus() throws ExecutionException, JsonProcessingException, InterruptedException {

        var job1 = new ProjectAllocationJob();
        var job2 = new ProjectAllocationJob();

        Mockito.when(jobRepository.findByNextExecutionTimeLessThanAndStatusIn(any(), anyList()))
                .thenReturn(Stream.of(job1, job2));

        jobPollingService.pollProjectAllocationJobs();

        // verify they are enqueued
        Mockito.verify(jobQueue, Mockito.times(2)).enqueue(jobCaptor.capture());

        // verify the status is correctly updated
        assertThat(job1.getStatus()).isEqualTo(JobStatus.ENQUEUED);
        assertThat(job2.getStatus()).isEqualTo(JobStatus.ENQUEUED);

        // verify their next execution time is updated
        assertThat(job1.getNextExecutionTime()).isCloseTo(Instant.now().plus(jobConfig.getMaxJobExecutionTime()), within(100, ChronoUnit.MILLIS));
        assertThat(job2.getNextExecutionTime()).isCloseTo(Instant.now().plus(jobConfig.getMaxJobExecutionTime()), within(100, ChronoUnit.MILLIS));
    }

    @Test
    void pollingServiceShouldThrowExceptionWhenNotAbleToQueryDB() throws ExecutionException, JsonProcessingException, InterruptedException {
        Mockito.when(jobRepository.findByNextExecutionTimeLessThanAndStatusIn(any(), anyList()))
                .thenThrow(new DataSourceLookupFailureException("TEST: db exception"));

        assertThatThrownBy(() -> jobPollingService.pollProjectAllocationJobs());

        // verify no jobs are enqueued
        Mockito.verify(jobQueue, Mockito.never()).enqueue(any());
    }

    @Test
    void pollingServiceShouldMarkTheJobAsFailedWhenNotAbleToEnqueue() throws ExecutionException, JsonProcessingException, InterruptedException {
        var job1 = new ProjectAllocationJob();
        var job2 = new ProjectAllocationJob();

        Mockito.when(jobRepository.findByNextExecutionTimeLessThanAndStatusIn(any(), anyList()))
                .thenReturn(Stream.of(job1, job2));
        Mockito.doThrow(new ExecutionException(new IOException("TEST (expected exception): enqueue exception"))).when(jobQueue).enqueue(any());

        jobPollingService.pollProjectAllocationJobs();

        // verify no jobs are enqueued
        Mockito.verify(jobQueue, Mockito.times(2)).enqueue(jobCaptor.capture());

        var actualJobs = jobCaptor.getAllValues();
        assertThat(actualJobs).hasSize(2);
        assertThatList(actualJobs).allSatisfy(job -> assertThat(job.getStatus()).isEqualTo(JobStatus.FAILED));
    }
}