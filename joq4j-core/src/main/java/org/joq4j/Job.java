package org.joq4j;

import java.util.Date;

public interface Job {

    String getId();

    JobOptions getOptions();

    JobStatus getStatus();

    String getOrigin();

    Date getEnqueuedAt();

    Date getStartedAt();

    Date getFinishedAt();

    String getWorkerId();

    Throwable getError() throws IllegalStateException;

    Object getResult() throws IllegalStateException;

    default boolean isStarted() {
        return this.getStatus() == JobStatus.STARTED;
    }

    default boolean isDone() {
        JobStatus status = this.getStatus();
        return status == JobStatus.SUCCESS
                || status == JobStatus.FAILURE
                || status == JobStatus.CANCELLED;
    }

    default boolean isCancelled() {
        return this.getStatus() == JobStatus.CANCELLED;
    }

    default boolean isSuccess() {
        return this.getStatus() == JobStatus.SUCCESS;
    }

    default boolean isFailure() {
        return this.getStatus() == JobStatus.FAILURE;
    }

    default boolean isDeleted() { return this.getStatus() == JobStatus.DELETED; }

    default boolean isExists() { return this.getStatus() != JobStatus.UNKNOWN; }
}
