package org.joq4j;

public interface Job {

    String getId();

    JobOptions getOptions();

    JobStatus getStatus();

    JobStateMeta getStateMeta();

    void updateState(JobStatus status);

    void updateState(JobStatus status, JobStateMeta meta);

    String getQueueName();

    String getOrigin();

    String getWorker();

    void perform();

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
