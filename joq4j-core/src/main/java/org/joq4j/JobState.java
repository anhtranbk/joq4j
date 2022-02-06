package org.joq4j;

import lombok.Getter;

public enum JobState {

    CREATED("created"),
    SCHEDULED("scheduled"),
    QUEUED("queued"),
    PAUSED("paused"),
    STARTED("started"),
    SUCCESS("success"),
    FAILURE("failure"),
    RETRY("retry"),
    CANCELLED("cancelled");

    @Getter
    private final String name;

    JobState(String name) {
        this.name = name.toUpperCase();
    }
}
