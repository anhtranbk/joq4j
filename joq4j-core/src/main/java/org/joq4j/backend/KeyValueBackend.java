package org.joq4j.backend;

import org.joq4j.Job;
import org.joq4j.JobExecutionException;
import org.joq4j.JobState;
import org.joq4j.common.utils.DateTimes;
import org.joq4j.common.utils.StringMap;

import java.util.HashMap;
import java.util.Map;

import static org.joq4j.Job.FIELD_DESCRIPTION;
import static org.joq4j.Job.FIELD_ERROR;
import static org.joq4j.Job.FIELD_FINISHED_AT;
import static org.joq4j.Job.FIELD_ID;
import static org.joq4j.Job.FIELD_NAME;
import static org.joq4j.Job.FIELD_QUEUED_AT;
import static org.joq4j.Job.FIELD_RESULT;
import static org.joq4j.Job.FIELD_STARTED_AT;
import static org.joq4j.Job.FIELD_STATE;
import static org.joq4j.Job.FIELD_TASK;
import static org.joq4j.Job.FIELD_WORKER;

public interface KeyValueBackend extends StorageBackend {

    String JOB_KEY_PREFIX = "jq:job:";

    void put(String key, Map<String, String> fieldMap);

    void putOne(String key, String field, String value);

    String getOne(String key, String field);

    Map<String, String> get(String key);

    String removeOne(String key, String field);

    Map<String, String> remove(String key);

    default void storeJob(Job job) {
        Map<String, String> fieldMap = new HashMap<>();

        fieldMap.put(FIELD_ID, job.id());
        fieldMap.put(FIELD_NAME, job.options().name());
        fieldMap.put(FIELD_DESCRIPTION, job.options().description());
        fieldMap.put(FIELD_TASK, job.task().getClass().getName());

        fieldMap.put(FIELD_WORKER, job.worker());
        fieldMap.put(FIELD_RESULT, "");
        fieldMap.put(FIELD_ERROR, "");

        fieldMap.put(FIELD_QUEUED_AT, "");
        fieldMap.put(FIELD_STARTED_AT, "");
        fieldMap.put(FIELD_FINISHED_AT, "");

        put(generateJobKey(job.id()), fieldMap);
    }

    default Job getJob(String jobId) {
        throw new UnsupportedOperationException();
    }

    default JobState getState(String jobId) {
        String state = getOne(generateJobKey(jobId), FIELD_STATE);
        return JobState.valueOf(state);
    }

    default void setState(String jobId, JobState status) {
        String now = DateTimes.currentDateTimeAsIsoString();
        StringMap map = new StringMap();
        switch (status) {
            case QUEUED:
                map.put(FIELD_QUEUED_AT, now);
                break;
            case STARTED:
                map.put(FIELD_STARTED_AT, now);
                break;
            case FAILURE:
            case SUCCESS:
                map.put(FIELD_FINISHED_AT, now);
                break;
            default:
                break;
        }
        map.put(FIELD_STATE, status.getName());
        put(generateJobKey(jobId), map);
    }

    default void markAsSuccess(String jobId, Object result) {
        StringMap map = new StringMap();
        map.put(FIELD_STATE, JobState.SUCCESS.getName());
        map.put(FIELD_FINISHED_AT, DateTimes.currentDateTimeAsIsoString());
        map.put(FIELD_RESULT, getTaskSerializer().writeAsBase64(result, Object.class));
        put(generateJobKey(jobId), map);
    }

    default void markAsFailure(String jobId, Throwable error) {
        StringMap map = new StringMap();
        map.put(FIELD_STATE, JobState.FAILURE.getName());
        map.put(FIELD_FINISHED_AT, DateTimes.currentDateTimeAsIsoString());
        map.put(FIELD_RESULT, error.getMessage());
        put(generateJobKey(jobId), map);
    }

    default JobExecutionException getError(String jobId) throws IllegalStateException {
        this.ensureJobFinished(jobId);
        String key = this.generateJobKey(jobId);
        return new JobExecutionException(this.getOne(key, FIELD_ERROR));
    }

    default Object getResult(String jobId) throws IllegalStateException {
        this.ensureJobFinished(jobId);
        return getOne(generateJobKey(jobId), FIELD_RESULT);
    }

    default String generateJobKey(String jobId) {
        return JOB_KEY_PREFIX + jobId;
    }
}
