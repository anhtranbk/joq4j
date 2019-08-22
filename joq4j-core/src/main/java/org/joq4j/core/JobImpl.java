package org.joq4j.core;

import org.joq4j.JobStateMeta;
import org.joq4j.Task;
import org.joq4j.Job;
import org.joq4j.JobOptions;
import org.joq4j.JobQueue;
import org.joq4j.JobStatus;
import org.joq4j.JobExecutionException;
import org.joq4j.backend.StorageBackend;
import org.joq4j.common.utils.DateTimes;
import org.joq4j.common.utils.Threads;
import org.joq4j.encoding.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

public class JobImpl implements Job {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobImpl.class);
    private static final String JOB_KEY_PREFIX = "jq:job:";

    static final String FIELD_STATUS = "status";
    static final String FIELD_QUEUED_AT = "queued_at";
    static final String FIELD_STARTED_AT = "started_at";
    static final String FIELD_FINISHED_AT = "finished_at";
    static final String FIELD_RESULT = "result";
    static final String FIELD_ERROR = "error";

    static final String FIELD_TASK = "task";
    static final String FIELD_WORKER = "worker";

    static final String FIELD_DESCRIPTION = "description";
    static final String FIELD_TIMEOUT = "timeout";

    private final String jobId;
    private final String jobKey;

    private final JobOptions options;
    private final JobQueueImpl queue;
    private final Serializer serializer;

    private StorageBackend backend;
    private Task task;
    private Map<String, String> meta = new HashMap<>();

    JobImpl(JobQueue queue) {
        this(queue, null, new JobOptions());
    }

    JobImpl(JobQueue queue, JobOptions options) {
        this(queue, null, options);
    }

    JobImpl(JobQueue queue, Task task, JobOptions options) {
        this.task = task;
        this.options = options;

        if (!(queue instanceof JobQueueImpl)) {
            throw new IllegalArgumentException(
                    "Queue argument must be an instance of org.joq4j.core.JobQueueImpl");
        }
        this.queue = (JobQueueImpl) queue;
        this.backend = this.queue.getBackend();
        this.serializer = this.queue.getSerializer();

        this.jobId = generateId();
        this.jobKey = generateJobKey(this.jobId);
        LOGGER.debug("New job has created: " + this.jobId);
    }

    public boolean cancel() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getId() {
        return this.jobId;
    }

    @Override
    public JobOptions getOptions() {
        return this.options;
    }

    @Override
    public JobStatus getStatus() {
        try {
            String status = getField(FIELD_STATUS);
            return JobStatus.valueOf(status);
        } catch (IllegalArgumentException | NullPointerException ignored) {
            return JobStatus.UNKNOWN;
        }
    }

    @Override
    public JobStateMeta getStateMeta() {
        return null;
    }

    @Override
    public void updateState(JobStatus status) {

    }

    @Override
    public void updateState(JobStatus status, JobStateMeta meta) {

    }

    @Override
    public String getQueueName() {
        return this.queue.getName();
    }

    @Override
    public String getOrigin() {
        return null;
    }

    public Date getEnqueuedAt() {
        String fd = getField(FIELD_QUEUED_AT);
        return DateTimes.parseFromIsoFormat(fd);
    }

    public Date getStartedAt() {
        String fd = getField(FIELD_STARTED_AT);
        return DateTimes.parseFromIsoFormat(fd);
    }

    public Date getFinishedAt() {
        String fd = getField(FIELD_FINISHED_AT);
        return DateTimes.parseFromIsoFormat(fd);
    }

    @Override
    public String getWorker() {
        return getField(FIELD_WORKER);
    }

    @Override
    public Throwable getError() throws IllegalStateException {
        if (!isDone()) {
            throw new IllegalStateException("Job is not finished");
        }
        return new JobExecutionException(getField(FIELD_ERROR));
    }

    @Override
    public Object getResult() throws IllegalStateException {
        if (!isDone()) {
            throw new IllegalStateException("Job is not finished");
        }
        String res = getField(FIELD_RESULT);
        return serializer.readFromBase64(res, Object.class);
    }

    public JobFieldMap dumps() {
        JobFieldMap fieldMap = new JobFieldMap();
        fieldMap.put(FIELD_DESCRIPTION, options.getDescription());
        fieldMap.put(FIELD_TIMEOUT, String.valueOf(options.getJobTimeout()));
        fieldMap.put(FIELD_TASK, serializer.writeAsBase64(task, Task.class));
        fieldMap.put(FIELD_STATUS, JobStatus.STARTED.getName());
        return fieldMap;
    }

    public void loads(Map<String, String> fieldMap) {
        options.setDescription(fieldMap.get(FIELD_DESCRIPTION));
        options.setJobTimeout(Long.parseLong(fieldMap.get(FIELD_TIMEOUT)));
        task = serializer.readFromBase64(fieldMap.get(FIELD_TASK), Task.class);
    }

    public void setStatus(JobStatus status) {
        String dt = DateTimes.currentDateTimeAsIsoString();
        switch (status) {
            case QUEUED:
                setField(FIELD_QUEUED_AT, dt);
                break;
            case STARTED:
                setField(FIELD_STARTED_AT, dt);
                break;
            case FAILURE:
            case SUCCESS:
                setField(FIELD_FINISHED_AT, dt);
                break;
            default:
                break;
        }
        setField(FIELD_STATUS, status.getName());
    }

    public void perform() {
        try {
            Object result = task.call();
            setField(FIELD_RESULT, serializer.writeAsBase64(result, Object.class));
            setStatus(JobStatus.SUCCESS);
        } catch (Throwable t) {
            setField(FIELD_ERROR, t.getMessage());
            setStatus(JobStatus.FAILURE);
        }
    }

    public Object waitForJobResult(long timeout) throws TimeoutException {
        long now;
        do {
            String value = getField(FIELD_RESULT);
            if (value != null) {
                return serializer.readFromBase64(value, Object.class);
            }
            Threads.sleep(1);
            now = System.currentTimeMillis();
        } while (System.currentTimeMillis() - now > timeout || timeout == 0);
        throw new TimeoutException();
    }

    private String getField(String field) {
        return backend.getFromMap(jobKey, field);
    }

    private void setField(String field, String value) {
        backend.putToMap(jobKey, field, value);
    }

    private Map<String, String> getBatchFields() {
        return backend.getMap(jobKey);
    }

    private void setBatchFields(Map<String, String> fieldMap) {
        backend.putMap(jobKey, fieldMap);
    }

    private static String generateJobKey(String jobId) {
        return JOB_KEY_PREFIX + jobId;
    }

    private static String generateId() {
        return UUID.randomUUID().toString();
    }
}
