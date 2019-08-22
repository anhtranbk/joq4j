package org.joq4j;

import lombok.Getter;

import java.io.Serializable;

public enum JobStatus implements Serializable {

    UNKNOWN("unknown"),
    QUEUED("queued"),
    STARTED("started"),
    SUCCESS("success"),
    FAILURE("failure"),
    CANCELLED("cancelled");

    @Getter
    private String name;

    JobStatus(String name) {
        this.name = name.toUpperCase();
    }
}
