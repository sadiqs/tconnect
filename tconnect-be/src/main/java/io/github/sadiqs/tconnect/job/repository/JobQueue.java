package io.github.sadiqs.tconnect.job.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.sadiqs.tconnect.job.model.ProjectAllocationJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;

@Component
@Slf4j
@RequiredArgsConstructor
public class JobQueue {

    public static final String JOB_QUEUE_NAME = "job_queue";

    private final ObjectMapper objectMapper;

    private final KafkaTemplate<String, String> kafkaTemplate;

    public void enqueue(ProjectAllocationJob job) throws JsonProcessingException, ExecutionException, InterruptedException {
        String message = objectMapper.writeValueAsString(job);
        kafkaTemplate.send(JOB_QUEUE_NAME, message).thenAccept(sendResult -> {
            log.debug("Successfully enqueued job for project {}", job.getProjectId());
        }).get();
    }
}
