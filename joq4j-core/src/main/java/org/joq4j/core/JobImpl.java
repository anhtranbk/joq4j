package org.joq4j.core;

import org.joq4j.AsyncTask;
import org.joq4j.Broker;
import org.joq4j.Job;
import org.joq4j.JobCallback;
import org.joq4j.JobOptions;
import org.joq4j.JobQueue;
import org.joq4j.JobStatus;
import org.joq4j.RemoteExecutionException;
import org.joq4j.common.utils.DateTimes;
import org.joq4j.common.utils.Threads;
import org.joq4j.serde.Deserializer;
import org.joq4j.serde.Serializer;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

public class JobImpl implements Job {

    private static final String JOB_KEY_PREFIX = "jq:job";

    private static final String FIELD_STATUS = "status";
    private static final String FIELD_QUEUED_AT = "queued_at";
    private static final String FIELD_STARTED_AT = "started_at";
    private static final String FIELD_FINISHED_AT = "finished_at";
    private static final String FIELD_RESULT = "result";
    private static final String FIELD_ERROR = "error";
    private static final String FIELD_WORKER_ID = "worker";

    private final String jobId;

    private final JobOptions options;
    private final JobCallback callback;
    private final AsyncTask task;

    private final JobQueueImpl queue;
    private final Broker broker;
    private final Serializer serializer;
    private final Deserializer deserializer;

    JobImpl(AsyncTask task, JobQueue queue, Broker broker, JobOptions options,
            JobCallback callback, String jobId) {
        this.task = task;
        this.options = options;
        this.callback = callback;
        this.broker = broker;

        if (!(queue instanceof JobQueueImpl)) {
            throw new IllegalArgumentException(
                    "Queue argument must be an instance of org.joq4j.core.JobQueueImpl");
        }
        this.queue = (JobQueueImpl) queue;
        this.serializer = queue.getOptions().getSerializer();
        this.deserializer = queue.getOptions().getDeserializer();

        if (jobId != null) {
            if (this.queue.isExists(jobId)) {
                throw new IllegalStateException("Job ID " + jobId + " already exists in queue");
            }
            this.jobId = jobId;
        } else {
            this.jobId = generateId();
        }
    }

    @Override
    public String getId() {
        return this.jobId;
    }

    @Override
    public String getDescription() {
        return this.options.getDescription();
    }

    @Override
    public JobStatus getStatus() {
        try {
            String status = getField(FIELD_STATUS);
            return JobStatus.valueOf(status.toUpperCase());
        } catch (Exception ignored) {
            return JobStatus.UNKNOWN;
        }
    }

    @Override
    public JobCallback getCallback() {
        return this.callback;
    }

    @Override
    public String getOrigin() {
        return this.queue.getName();
    }

    @Override
    public Date getEnqueuedAt() {
        String fd = getField(FIELD_QUEUED_AT);
        return DateTimes.parseFromIsoFormat(fd);
    }

    @Override
    public Date getStartedAt() {
        String fd = getField(FIELD_STARTED_AT);
        return DateTimes.parseFromIsoFormat(fd);
    }

    @Override
    public Date getFinishedAt() {
        String fd = getField(FIELD_FINISHED_AT);
        return DateTimes.parseFromIsoFormat(fd);
    }

    @Override
    public String getWorkerId() {
        return getField(FIELD_WORKER_ID);
    }

    @Override
    public Throwable getError() throws IllegalStateException {
        if (!isDone()) {
            throw new IllegalStateException("Job is not finished");
        }
        return new RemoteExecutionException(getField(FIELD_ERROR));
    }

    @Override
    public Object getResult() throws IllegalStateException {
        if (!isDone()) {
            throw new IllegalStateException("Job is not finished");
        }
        String res = getField(FIELD_RESULT);
        return deserializer.deserializeFromBase64(res);
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

    public Object perform() {
        try {
            Object result = task.call();
            setField(FIELD_RESULT, serializer.serializeAsBase64(result));
            setStatus(JobStatus.SUCCESS);
            return result;
        } catch (Throwable t) {
            setField(FIELD_ERROR, t.getMessage());
            setStatus(JobStatus.FAILURE);
        }
        return null;
    }

    public Object waitForJobResult(long timeout) throws TimeoutException {
        long now;
        do {
            String value = getField(FIELD_RESULT);
            if (value != null) {
                return deserializer.deserializeFromBase64(value);
            }
            Threads.sleep(1);
            now = System.currentTimeMillis();
        } while (System.currentTimeMillis() - now > timeout || timeout == 0);
        throw new TimeoutException();
    }

    private String getField(String field) {
        return broker.getFromMap(generateJobKey(this.jobId), field);
    }

    private void setField(String field, String value) {
        broker.putToMap(generateJobKey(this.jobId), field, value);
    }

    public String generateId() {
        return UUID.randomUUID().toString();
    }

    public static String generateJobKey(String jobId) {
        return JOB_KEY_PREFIX + ":" + jobId;
    }
}
