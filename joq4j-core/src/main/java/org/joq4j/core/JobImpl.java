package org.joq4j.core;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.joq4j.Job;
import org.joq4j.JobExecutionException;
import org.joq4j.Task;
import org.joq4j.TaskOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.UUID;

@Accessors(fluent = true)
@Getter
public class JobImpl implements Job {

    private static final Logger logger = LoggerFactory.getLogger(JobImpl.class);

    private TaskOptions options;
    private String queueName;
    private String id;
    private String worker = "";
    private Task task;

    private Date enqueuedAt;
    private Date startedAt;
    private Date finishedAt;

    private Object result;
    private Throwable error;

    JobImpl(String queueName, Task task) {
        this(queueName, task, new TaskOptions());
    }

    JobImpl(String queueName, Task task, TaskOptions options) {
        this.task = task;
        this.options = options;
        this.queueName = queueName;
        this.id = generateId();
        logger.debug("New job has created: " + this.id);
    }

    @Override
    public Object perform() {
        try {
            this.result =  task.call();
            return result;
        } catch (Throwable t) {
            this.error = t;
            throw new JobExecutionException(t);
        }
    }

    private static String generateId() {
        return UUID.randomUUID().toString();
    }
}
