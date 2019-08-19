package org.joq4j.internal;

import org.joq4j.AsyncTask;
import org.joq4j.Broker;
import org.joq4j.Job;
import org.joq4j.JobOptions;
import org.joq4j.JobQueue;
import org.joq4j.JobStatus;
import org.joq4j.RemoteExecutionException;
import org.joq4j.common.utils.DateTimes;
import org.joq4j.common.utils.Threads;
import org.joq4j.serde.Deserializer;
import org.joq4j.serde.JavaSerdeFactory;
import org.joq4j.serde.Serializer;
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

    private static final String FIELD_STATUS = "status";
    private static final String FIELD_QUEUED_AT = "queued_at";
    private static final String FIELD_STARTED_AT = "started_at";
    private static final String FIELD_FINISHED_AT = "finished_at";
    private static final String FIELD_RESULT = "result";
    private static final String FIELD_ERROR = "error";

    private static final String FIELD_TASK = "task";
    private static final String FIELD_WORKER_ID = "worker";

    private static final String FIELD_DESCRIPTION = "description";
    private static final String FIELD_TIMEOUT = "timeout";
    private static final String FIELD_TTL = "ttl";
    private static final String FIELD_RESULT_TTL = "result_ttl";
    private static final String FIELD_FAILURE_TTL = "fail_ttl";

    private final String jobId;
    private AsyncTask task;

    private final JobOptions options;
    private final JobQueueImpl queue;
    private final Broker broker;
    private final Serializer serializer;
    private final Deserializer deserializer;

    JobImpl(JobQueue queue, AsyncTask task, JobOptions options) {
        this.task = task;
        this.options = options;
        if (this.options.getDescription() == null) {
            this.options.setDescription(task.toString());
        }

        if (!(queue instanceof JobQueueImpl)) {
            throw new IllegalArgumentException(
                    "Queue argument must be an instance of org.joq4j.core.JobQueueImpl");
        }
        this.queue = (JobQueueImpl) queue;
        this.broker = this.queue.getBroker();
        this.serializer = this.queue.getSerializer();
        this.deserializer = this.queue.getDeserializer();

        final String jobId = options.getJobId();
        if (jobId != null) {
            if (jobId.length() < 4 || jobId.length() > 128) {
                throw new IllegalArgumentException("Job ID length must be between 4 to 128 characters");
            }
            if (this.queue.isExists(jobId)) {
                throw new IllegalStateException("Job ID " + jobId + " already exists in queue");
            }
            this.jobId = jobId;
        } else {
            this.jobId = generateId();
        }
        LOGGER.debug("New job has created: " + this.jobId);
        System.out.println("New job " + this.jobId);
    }

    @Override
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
            return JobStatus.valueOf(status.toUpperCase());
        } catch (Exception ignored) {
            return JobStatus.UNKNOWN;
        }
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

    public void initNewJob() {
        Map<String, String> fieldMap = new HashMap<>();
        fieldMap.put(FIELD_DESCRIPTION, options.getDescription());
        fieldMap.put(FIELD_TIMEOUT, String.valueOf(options.getJobTimeout()));
        fieldMap.put(FIELD_TTL, String.valueOf(options.getTtl()));
        fieldMap.put(FIELD_RESULT_TTL, String.valueOf(options.getResultTtl()));
        fieldMap.put(FIELD_FAILURE_TTL, String.valueOf(options.getFailureTtl()));
        fieldMap.put(FIELD_TASK, new JavaSerdeFactory().createSerializer().serializeAsBase64(task));

        setBatchFields(fieldMap);
        setStatus(JobStatus.QUEUED);
    }

    public void restoreFromBroker() {
        Map<String, String> fieldMap = getBatchFields();
        options.setDescription(fieldMap.get(FIELD_DESCRIPTION));
        options.setJobTimeout(Long.parseLong(fieldMap.get(FIELD_TIMEOUT)));
        options.setTtl(Long.parseLong(fieldMap.get(FIELD_TTL)));
        options.setResultTtl(Long.parseLong(fieldMap.get(FIELD_RESULT_TTL)));
        options.setFailureTtl(Long.parseLong(fieldMap.get(FIELD_FAILURE_TTL)));

        String serialized = fieldMap.get(FIELD_TASK);
        task = (AsyncTask) new JavaSerdeFactory().createDeserializer().deserializeFromBase64(serialized);
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

    public String generateId() {
        return UUID.randomUUID().toString();
    }

    public static String generateJobKey(String jobId) {
        return JOB_KEY_PREFIX + jobId;
    }

    private String getField(String field) {
        return broker.getFromMap(generateJobKey(this.jobId), field);
    }

    private void setField(String field, String value) {
        broker.putToMap(generateJobKey(this.jobId), field, value);
    }

    private Map<String, String> getBatchFields() {
        return broker.getMap(generateJobKey(this.jobId));
    }

    private void setBatchFields(Map<String, String> fieldMap) {
        broker.putMap(generateJobKey(this.jobId), fieldMap);
    }
}
