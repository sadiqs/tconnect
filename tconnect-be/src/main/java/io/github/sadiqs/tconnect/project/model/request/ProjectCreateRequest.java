package io.github.sadiqs.tconnect.project.model.request;

import java.time.Instant;

public record ProjectCreateRequest(String title, String description, int expectedHours, Instant biddingEndTime) {
}
