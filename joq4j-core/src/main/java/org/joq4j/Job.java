package org.joq4j;

import java.util.Map;

public interface Job {

    String FIELD_STATUS = "status";
    String FIELD_RESULT = "result";
    String FIELD_ERROR = "error";
    String FIELD_QUEUED_AT = "queued_at";
    String FIELD_STARTED_AT = "started_at";
    String FIELD_FINISHED_AT = "finished_at";
    String FIELD_DATA = "data";
    String FIELD_WORKER = "worker";

    String FIELD_ID = "id";
    String FIELD_NAME = "name";
    String FIELD_DESCRIPTION = "desc";
    String FIELD_TIMEOUT = "timeout";
    String FIELD_MAX_RETRIES = "max_retries";
    String FIELD_RETRY_DELAY = "retry_delay";
    String FIELD_PRIORITY = "priority";

    String getId();

    JobOptions getOptions();

    String getQueueName();

    String getWorker();

    Map<String, String> dumps();

    void loads(Map<String, String> fieldMap);

    Object perform();
}
