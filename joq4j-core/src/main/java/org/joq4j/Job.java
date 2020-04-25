package org.joq4j;

import java.util.Map;

public interface Job {

    String FIELD_STATE = "state";
    String FIELD_RESULT = "result";
    String FIELD_ERROR = "error";
    String FIELD_QUEUED_AT = "queued_at";
    String FIELD_STARTED_AT = "started_at";
    String FIELD_FINISHED_AT = "finished_at";
    String FIELD_WORKER = "worker";
    String FIELD_TASK = "task";

    String FIELD_ID = "id";
    String FIELD_NAME = "name";
    String FIELD_DESCRIPTION = "desc";
    String FIELD_TIMEOUT = "timeout";
    String FIELD_MAX_RETRIES = "max_retries";
    String FIELD_RETRY_DELAY = "retry_delay";
    String FIELD_PRIORITY = "priority";

    String id();

    TaskOptions options();

    JobState state();

    String queueName();

    Task task();

    String worker();

    Map<String, String> dumps();

    void loads(Map<String, String> fieldMap);

    Object perform();

    Throwable error() throws IllegalStateException;

    Object result() throws IllegalStateException;

    default boolean isStarted() {
        return this.state() == JobState.STARTED;
    }

    default boolean isDone() {
        JobState state = this.state();
        return state == JobState.SUCCESS || state == JobState.FAILURE
                || state == JobState.CANCELLED;
    }

    default boolean isSuccess() {
        return this.state() == JobState.SUCCESS;
    }

    default boolean isFailure() {
        return this.state() == JobState.FAILURE;
    }

    default boolean isCancelled() {
        return this.state() == JobState.CANCELLED;
    }
}
