package org.joq4j.backend;

import org.joq4j.Job;
import org.joq4j.JobBuilder;
import org.joq4j.JobExecutionException;
import org.joq4j.JobState;
import org.joq4j.Task;
import org.joq4j.common.utils.DateTimes;
import org.joq4j.common.utils.StringMap;

import java.util.HashMap;
import java.util.Map;

public interface KeyValueBackend extends StorageBackend {

    String JOB_KEY_PREFIX = "jq:job:";

    void put(String key, Map<String, String> fieldMap);

    void putOne(String key, String field, String value);

    String getOne(String key, String field);

    Map<String, String> get(String key);

    String removeOne(String key, String field);

    Map<String, String> remove(String key);

    default void storeJob(Job job) {
        Map<String, String> fieldMap = new HashMap<String, String>() {{
            put(FIELD_ID, job.id());
            put(FIELD_GROUP_ID, job.groupId());
            put(FIELD_NAME, job.name());
            put(FIELD_DESCRIPTION, job.description());

            put(FIELD_ETA, String.valueOf(job.eta()));
            put(FIELD_TIMEOUT, String.valueOf(job.timeout()));
            put(FIELD_MAX_RETRIES, String.valueOf(job.maxRetries()));
            put(FIELD_RETRY_DELAY, String.valueOf(job.retryDelay()));
            put(FIELD_PRIORITY, String.valueOf(job.priority()));

            put(FIELD_STATE, JobState.CREATED.getName());
        }};

        put(generateJobKey(job.id()), fieldMap);
        if (!job.meta().isEmpty()) {
            put(generateJobMetaKey(job.id()), job.meta());
        }
    }

    @Override
    default Job fetchJob(String jobId) {
        Map<String, String> fieldMap = get(generateJobKey(jobId));
        JobBuilder builder = Job.builder()
                .id(fieldMap.get(FIELD_ID))
                .groupId(fieldMap.get(FIELD_GROUP_ID))
                .name(fieldMap.get(FIELD_NAME))
                .description(fieldMap.getOrDefault(FIELD_DESCRIPTION, ""))
                .task(Task.doNothing())
                .eta(Long.parseLong(fieldMap.get(FIELD_ETA)))
                .timeout(fieldMap.get(FIELD_TIMEOUT))
                .maxRetries(Integer.parseInt(fieldMap.get(FIELD_MAX_RETRIES)))
                .retryDelay(Long.parseLong(fieldMap.get(FIELD_RETRY_DELAY)))
                .priority(Integer.parseInt(fieldMap.get(FIELD_PRIORITY)));

        Map<String, String> metaMap = get(generateJobMetaKey(jobId));
        if (metaMap != null && !metaMap.isEmpty()) {
            builder.meta(metaMap);
        }

        return builder.build();
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

    default void setWorker(String jobId, String worker) {
        putOne(generateJobKey(jobId), FIELD_WORKER, worker);
    }

    default void markAsSuccess(String jobId, Object result) {
        StringMap map = new StringMap();
        map.put(FIELD_STATE, JobState.SUCCESS.getName());
        map.put(FIELD_FINISHED_AT, DateTimes.currentDateTimeAsIsoString());
        map.put(FIELD_RESULT, result.toString());
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

    default String generateJobMetaKey(String jobId) {
        return JOB_KEY_PREFIX + jobId + ":meta";
    }
}
