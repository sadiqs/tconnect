package io.github.sadiqs.tconnect.job.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.Duration;

@Data
@Configuration
@EnableScheduling
public class JobConfig {
    @Value("${job.max.execution.time}")
    private Duration maxJobExecutionTime; // beyond which it will be considered to be failed/lost
}

