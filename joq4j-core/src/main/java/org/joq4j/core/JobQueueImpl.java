package org.joq4j.core;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.joq4j.AsyncResult;
import org.joq4j.Job;
import org.joq4j.JobBuilder;
import org.joq4j.JobCallback;
import org.joq4j.JobQueue;
import org.joq4j.JobState;
import org.joq4j.QueueOptions;
import org.joq4j.Task;
import org.joq4j.backend.StorageBackend;
import org.joq4j.broker.Broker;
import org.joq4j.config.Config;
import org.joq4j.serde.MessageEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Accessors(fluent = true)
@Getter
public class JobQueueImpl implements JobQueue {
    private static final Logger logger = LoggerFactory.getLogger(JobQueueImpl.class);
    private static final String QUEUE_KEY_PREFIX = "jq:queue:";

    private final String name;
    private final String queueKey;
    private final Broker broker;
    private final StorageBackend backend;

    private final long defaultTimeout;
    private final MessageEncoder messageEncoder;

    public JobQueueImpl(String name, Broker broker, StorageBackend backend, Config conf) {
        this.name = name;
        this.broker = broker;
        this.backend = backend;
        this.queueKey = QUEUE_KEY_PREFIX + this.name;

        QueueOptions options = new QueueOptions();
        options.configure(conf);

        this.defaultTimeout = options.defaultTimeout();
        this.messageEncoder = options.messageEncoder();
    }

    @Override
    public AsyncResult enqueue(Task task, String name) {
        return enqueue(Job.builder().name(name).task(task).build());
    }

    @Override
    public AsyncResult enqueue(Task task) {
        return enqueue(Job.builder().task(task).build());
    }

    @Override
    public AsyncResult enqueue(Task task, JobCallback callback) {
        Job job = Job.builder()
                .task(task)
                .jobCallback(callback)
                .build();
        return enqueue(job);
    }

    @Override
    public AsyncResult enqueue(Job job) {
        broker.push(queueKey, messageEncoder.writeAsString(job));
        logger.debug("Job enqueued: " + job);

        backend.storeJob(job);
        backend.setState(job.id(), JobState.QUEUED);

        return new AsyncResultImpl(backend, job);
    }

    @Override
    public Job pop(String worker) {
        String serializedJob = broker.pop(queueKey);
        Job job = messageEncoder.read(serializedJob, JobBuilder.class).build();
        backend.setWorker(job.id(), worker);
        return job;
    }
}
