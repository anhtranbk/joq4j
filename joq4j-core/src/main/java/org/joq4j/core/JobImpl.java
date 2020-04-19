package org.joq4j.core;

import lombok.Getter;
import org.joq4j.Job;
import org.joq4j.JobExecutionException;
import org.joq4j.JobOptions;
import org.joq4j.JobQueue;
import org.joq4j.Task;
import org.joq4j.encoding.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class JobImpl implements Job {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobImpl.class);

    private final JobOptions options;
    private final JobQueueImpl queue;
    private final Serializer serializer;

    private String id;
    private String worker = "";
    private Task task;

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
        this.serializer = this.queue.getSerializer();

        this.id = generateId();
        LOGGER.debug("New job has created: " + this.id);
    }

    @Override
    public String getQueueName() {
        return this.queue.getName();
    }

    @Override
    public Map<String, String> dumps() {
        Map<String, String> fieldMap = new HashMap<>();
        fieldMap.put(FIELD_NAME, options.getName());
        fieldMap.put(FIELD_DESCRIPTION, options.getDescription());
        fieldMap.put(FIELD_TIMEOUT, String.valueOf(options.getJobTimeout()));
        fieldMap.put(FIELD_MAX_RETRIES, String.valueOf(options.getMaxRetries()));
        fieldMap.put(FIELD_RETRY_DELAY, String.valueOf(options.getRetryDelay()));
        fieldMap.put(FIELD_PRIORITY, String.valueOf(options.getPriority()));

        fieldMap.put(FIELD_DATA, serializer.writeAsBase64(task, Task.class));
        fieldMap.put(FIELD_WORKER, worker);
        fieldMap.put(FIELD_ID, getId());
        return fieldMap;
    }

    @Override
    public void loads(Map<String, String> fieldMap) {
        options.setName(fieldMap.get(FIELD_NAME));
        options.setDescription(fieldMap.get(FIELD_DESCRIPTION));
        options.setJobTimeout(Long.parseLong(fieldMap.get(FIELD_TIMEOUT)));
        options.setMaxRetries(Integer.parseInt(fieldMap.get(FIELD_MAX_RETRIES)));
        options.setRetryDelay(Long.parseLong(fieldMap.get(FIELD_RETRY_DELAY)));
        options.setPriority(Integer.parseInt(fieldMap.get(FIELD_PRIORITY)));

        task = serializer.readFromBase64(fieldMap.get(FIELD_DATA), Task.class);
        worker = fieldMap.get(FIELD_WORKER);
        id = fieldMap.get(FIELD_ID);
    }

    @Override
    public Object perform() {
        try {
            return task.call();
        } catch (Throwable t) {
            throw new JobExecutionException(t);
        }
    }

    private static String generateId() {
        return UUID.randomUUID().toString();
    }
}
