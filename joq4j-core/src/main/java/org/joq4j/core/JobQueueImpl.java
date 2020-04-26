package org.joq4j.core;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.joq4j.AsyncResult;
import org.joq4j.Job;
import org.joq4j.JobQueue;
import org.joq4j.JobState;
import org.joq4j.QueueOptions;
import org.joq4j.Task;
import org.joq4j.TaskOptions;
import org.joq4j.backend.NullBackend;
import org.joq4j.backend.StorageBackend;
import org.joq4j.broker.Broker;
import org.joq4j.broker.MemoryBroker;
import org.joq4j.config.Config;
import org.joq4j.encoding.Encoder;
import org.joq4j.encoding.TaskSerializer;

@Accessors(fluent = true)
@Getter
public class JobQueueImpl implements JobQueue {

    private static final String QUEUE_KEY_PREFIX = "jq:queue:";

    private final String name;
    private final String queueKey;
    private final Broker broker;
    private final StorageBackend backend;

    private final long defaultTimeout;
    private final Encoder jobEncoder;
    private final TaskSerializer taskSerializer;

    public JobQueueImpl(String name, Broker broker, StorageBackend backend,
                        Encoder jobEncoder, TaskSerializer taskSerializer, long defaultTimeout) {
        this.name = name;
        this.queueKey = QUEUE_KEY_PREFIX + this.name;
        this.defaultTimeout = defaultTimeout;
        this.broker = broker;
        this.backend = backend;
        this.jobEncoder = jobEncoder;
        this.taskSerializer = taskSerializer;
    }

    public JobQueueImpl(Config conf) {
        QueueOptions options = new QueueOptions();
        options.configure(conf);

        this.name = options.name();
        this.queueKey = QUEUE_KEY_PREFIX + this.name;
        this.defaultTimeout = options.defaultTimeout();
        this.broker = new MemoryBroker();
        this.backend = new NullBackend();
        this.jobEncoder = options.encoder();
        this.taskSerializer = options.taskSerializer();
    }

    @Override
    public AsyncResult enqueue(Task task) {
        return enqueue(task, new TaskOptions().timeout(this.defaultTimeout));
    }

    @Override
    public AsyncResult enqueue(Task task, TaskOptions options) {
        JobImpl job = new JobImpl(this.name, task, options);
        broker.push(queueKey, jobEncoder.writeAsBase64(job));
        backend.setState(job.id(), JobState.QUEUED);
        return new AsyncResultImpl(backend, job);
    }

    @Override
    public Job nextJob(String worker) {
        return jobEncoder.readFromBase64(broker.pop(queueKey));
    }
}
