package io.github.sadiqs.tconnect.job.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.github.sadiqs.tconnect.job.model.JobStatus;
import io.github.sadiqs.tconnect.job.model.ProjectAllocationJob;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.io.IOException;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static io.github.sadiqs.tconnect.job.repository.JobQueue.JOB_QUEUE_NAME;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
class JobQueueTest {

    @Mock
    KafkaTemplate<String, String> kafkaTemplate;

    ObjectMapper objectMapper = new ObjectMapper();

    JobQueue jobQueue;

    @BeforeEach
    void setUp() {
        Mockito.reset(kafkaTemplate);
        objectMapper.registerModule(new JavaTimeModule());
        jobQueue = new JobQueue(objectMapper, kafkaTemplate);
    }

    @Test
    void jobQueueShouldSerializeAndEnqueueTheGivenJob() throws ExecutionException, JsonProcessingException, InterruptedException {
        ProducerRecord<String, String> producerRecord = new ProducerRecord<>(JOB_QUEUE_NAME, "test");
        RecordMetadata recordMetadata = new RecordMetadata(new TopicPartition(JOB_QUEUE_NAME, 1), 0, 0, 0, 0, 10);

        Mockito.when(kafkaTemplate.send(any(), any())).thenReturn(
                CompletableFuture.completedFuture(new SendResult<>(producerRecord, recordMetadata)));

        ProjectAllocationJob job = new ProjectAllocationJob();
        job.setProjectId(UUID.randomUUID());
        job.setNextExecutionTime(Instant.now().plusSeconds(5));
        job.setStatus(JobStatus.CREATED);
        job.setInfo("test");

        jobQueue.enqueue(job);

        Mockito.verify(kafkaTemplate).send(eq(JOB_QUEUE_NAME), any());
    }

    @Test
    void jobQueueShouldThrowExceptionIfUnableToEnqueue() {
        Mockito.when(kafkaTemplate.send(any(), any())).thenReturn(
                CompletableFuture.failedFuture(new IOException("TEST: kafka error")));

        ProjectAllocationJob job = new ProjectAllocationJob();
        job.setProjectId(UUID.randomUUID());
        job.setNextExecutionTime(Instant.now().plusSeconds(5));
        job.setStatus(JobStatus.CREATED);
        job.setInfo("test");

        Assertions.assertThrows(ExecutionException.class, () -> {
            jobQueue.enqueue(job);
        });
    }
}