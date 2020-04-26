package org.joq4j;

import lombok.Getter;

import java.io.Serializable;

public enum JobState implements Serializable {

    UNKNOWN("unknown"),
    QUEUED("queued"),
    REVOKED("revoked"),
    STARTED("started"),
    SUCCESS("success"),
    FAILURE("failure"),
    CANCELLED("cancelled");

    @Getter
    private String name;

    JobState(String name) {
        this.name = name.toUpperCase();
    }
}
