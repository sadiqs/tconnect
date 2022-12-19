package io.github.sadiqs.tconnect.project.model;

import jakarta.persistence.Transient;

public interface AppUser {
    @Transient
    default String getType() {
        return getClass().getSimpleName().toLowerCase();
    }
}
