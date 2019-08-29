package org.joq4j.backend;

import org.joq4j.Job;
import org.joq4j.JobExecutionException;
import org.joq4j.JobStatus;
import org.joq4j.common.utils.DateTimes;
import org.joq4j.common.utils.StringMap;

import static org.joq4j.Job.*;

import java.util.Map;

public interface KeyValueBackend extends StorageBackend {

    String JOB_META_KEY_PREFIX = "jq:job:meta:";
    String JOB_PROPERTY_PREFIX = "jq:job:prop:";

    void putMap(String key, Map<String, String> fieldMap);

    void putToMap(String key, String field, String value);

    String getFromMap(String key, String field);

    Map<String, String> getMap(String key);

    String removeFromMap(String key, String field);

    Map<String, String> removeMap(String key);

    default void storeJob(Job job) {
        Map<String, String> map = job.dumps();
        putMap(generateJobMetaKey(job.getId()), map);
    }

    default JobStatus getStatus(String jobId) {
        String status = getFromMap(generateJobMetaKey(jobId), FIELD_STATUS);
        return JobStatus.valueOf(status);
    }

    default void updateStatus(String jobId, JobStatus status) {
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
        map.put(FIELD_STATUS, status.getName());
        putMap(generateJobMetaKey(jobId), map);
    }

    default Map<String, String> getMeta(String jobId) {
        return getMap(generateJobMetaKey(jobId));
    }

    default void updateMeta(String jobId, Map<String, String> meta) {
        putMap(generateJobMetaKey(jobId), meta);
    }

    default void markAsSuccess(String jobId, Object result) {
        StringMap map = new StringMap();
        map.put(FIELD_STATUS, JobStatus.SUCCESS.getName());
        map.put(FIELD_FINISHED_AT, DateTimes.currentDateTimeAsIsoString());
        map.put(FIELD_RESULT, getSerializer().writeAsBase64(result, Object.class));
        putMap(generateJobMetaKey(jobId), map);
    }

    default void markAsFailure(String jobId, Throwable error) {
        StringMap map = new StringMap();
        map.put(FIELD_STATUS, JobStatus.FAILURE.getName());
        map.put(FIELD_FINISHED_AT, DateTimes.currentDateTimeAsIsoString());
        map.put(FIELD_RESULT, error.getMessage());
        putMap(generateJobMetaKey(jobId), map);
    }

    default JobExecutionException getError(String jobId) throws IllegalStateException {
        ensureJobFinished(jobId);
        return new JobExecutionException(getFromMap(generateJobMetaKey(jobId), FIELD_ERROR));
    }

    default Object getResult(String jobId) throws IllegalStateException {
        ensureJobFinished(jobId);
        return getFromMap(generateJobMetaKey(jobId), FIELD_RESULT);
    }

    default String generateJobMetaKey(String jobId) {
        return JOB_META_KEY_PREFIX + jobId;
    }

    default String generateJobPropertyKey(String jobId) {
        return JOB_PROPERTY_PREFIX + jobId;
    }
}