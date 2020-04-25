package org.joq4j.core;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.joq4j.Job;
import org.joq4j.JobExecutionException;
import org.joq4j.JobState;
import org.joq4j.TaskOptions;
import org.joq4j.JobQueue;
import org.joq4j.Task;
import org.joq4j.encoding.TaskSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Accessors(fluent = true)
@Getter
public class JobImpl implements Job {

    private static final Logger logger = LoggerFactory.getLogger(JobImpl.class);

    private final TaskOptions options;
    private final JobQueueImpl queue;
    private final TaskSerializer taskSerializer;

    private String id;
    private String worker = "";
    private Task task;

    private Date enqueuedAt;
    private Date startedAt;
    private Date finishedAt;

    JobImpl(JobQueue queue) {
        this(queue, null, new TaskOptions());
    }

    JobImpl(JobQueue queue, TaskOptions options) {
        this(queue, null, options);
    }

    JobImpl(JobQueue queue, Task task, TaskOptions options) {
        this.task = task;
        this.options = options;

        if (!(queue instanceof JobQueueImpl)) {
            throw new IllegalArgumentException(
                    "Queue argument must be an instance of " + JobQueueImpl.class.getName());
        }
        this.queue = (JobQueueImpl) queue;
        this.taskSerializer = this.queue.getTaskSerializer();

        this.id = generateId();
        logger.debug("New job has created: " + this.id);
    }

    @Override
    public JobState state() {
        return null;
    }

    @Override
    public String queueName() {
        return this.queue.getName();
    }

    @Override
    public Map<String, String> dumps() {
        Map<String, String> fieldMap = new HashMap<>();
        fieldMap.put(FIELD_NAME, options.name());
        fieldMap.put(FIELD_DESCRIPTION, options.description());
        fieldMap.put(FIELD_TIMEOUT, String.valueOf(options.timeout()));
        fieldMap.put(FIELD_MAX_RETRIES, String.valueOf(options.maxRetries()));
        fieldMap.put(FIELD_RETRY_DELAY, String.valueOf(options.retryDelay()));
        fieldMap.put(FIELD_PRIORITY, String.valueOf(options.priority()));

        fieldMap.put(FIELD_TASK, taskSerializer.writeAsBase64(task, Task.class));
        fieldMap.put(FIELD_WORKER, worker);
        fieldMap.put(FIELD_ID, id());
        return fieldMap;
    }

    @Override
    public void loads(Map<String, String> fieldMap) {
        options.name(fieldMap.get(FIELD_NAME))
            .description(fieldMap.get(FIELD_DESCRIPTION))
            .timeout(Long.parseLong(fieldMap.get(FIELD_TIMEOUT)))
            .maxRetries(Integer.parseInt(fieldMap.get(FIELD_MAX_RETRIES)))
            .retryDelay(Long.parseLong(fieldMap.get(FIELD_RETRY_DELAY)))
            .priority(Integer.parseInt(fieldMap.get(FIELD_PRIORITY)));

        task = taskSerializer.readFromBase64(fieldMap.get(FIELD_TASK), Task.class);
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

    @Override
    public Throwable error() throws IllegalStateException {
        return null;
    }

    @Override
    public Object result() throws IllegalStateException {
        return null;
    }

    private static String generateId() {
        return UUID.randomUUID().toString();
    }
}
