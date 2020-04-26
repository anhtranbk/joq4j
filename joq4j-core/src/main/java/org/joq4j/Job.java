package org.joq4j;

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

    String queueName();

    Task task();

    String worker();

    Object perform();

    Throwable error();

    Object result();
}
