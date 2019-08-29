package org.joq4j;

public interface RunnableJob extends Job {

    JobStatus getStatus();

    Object perform();

    void markAsSuccess(Object result);

    void markAsFailure(Throwable error);

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
}
