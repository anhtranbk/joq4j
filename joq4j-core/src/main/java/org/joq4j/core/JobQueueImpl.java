package org.joq4j.core;

import lombok.Getter;
import org.joq4j.AsyncResult;
import org.joq4j.Job;
import org.joq4j.JobQueue;
import org.joq4j.JobState;
import org.joq4j.Task;
import org.joq4j.TaskOptions;
import org.joq4j.backend.StorageBackend;
import org.joq4j.broker.Broker;
import org.joq4j.encoding.Encoder;
import org.joq4j.encoding.TaskSerializer;

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

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public AsyncResult enqueue(Task task) {
        return enqueue(task, new TaskOptions().timeout(this.defaultTimeout));
    }

    @Override
    public AsyncResult enqueue(Task task, TaskOptions options) {
        JobImpl job = new JobImpl(this, task, options);
        broker.push(queueKey, jobEncoder.writeAsBase64(job.dumps()));
        backend.setState(job.id(), JobState.QUEUED);
        return new AsyncResultImpl(backend, job);
    }

    @Override
    public Job nextJob(String worker) {
        return jobEncoder.readFromBase64(broker.pop(queueKey));
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public void clear() {

    }
}
