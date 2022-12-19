package io.github.sadiqs.tconnect.project.model.request;

import java.util.UUID;

public record BidCreateRequest(UUID projectId, int amount) {
}