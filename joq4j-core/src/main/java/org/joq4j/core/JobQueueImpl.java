package org.joq4j.core;

import lombok.Getter;
import org.joq4j.AsyncResult;
import org.joq4j.Broker;
import org.joq4j.Job;
import org.joq4j.JobOptions;
import org.joq4j.JobQueue;
import org.joq4j.JobStatus;
import org.joq4j.Task;
import org.joq4j.backend.StorageBackend;
import org.joq4j.encoding.Encoder;
import org.joq4j.encoding.Serializer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public class JobQueueImpl implements JobQueue {

    private static final String QUEUE_KEY_PREFIX = "jq:queue:";

    private final String name;
    private final String queueKey;
    private final Broker broker;
    private final StorageBackend backend;

    private final long defaultTimeout;
    private final Encoder jobEncoder;
    private final Serializer serializer;

    public JobQueueImpl(String name, Broker broker, StorageBackend backend,
                        Encoder jobEncoder, Serializer serializer, long defaultTimeout) {
        this.name = name;
        this.queueKey = QUEUE_KEY_PREFIX + this.name;
        this.defaultTimeout = defaultTimeout;
        this.broker = broker;
        this.backend = backend;
        this.jobEncoder = jobEncoder;
        this.serializer = serializer;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public List<String> getAllJobIds() {
        List<Job> jobs = getAllJobs();
        List<String> jobIds = new ArrayList<>(jobs.size());
        for (Job job : jobs) {
            jobIds.add(job.getId());
        }
        return jobIds;
    }

    @Override
    public List<Job> getAllJobs() {
        List<String> encodedList = broker.getList(queueKey);
        if (encodedList == null || encodedList.isEmpty()) return Collections.emptyList();

        List<Job> jobs = new ArrayList<>(encodedList.size());
        for (String encoded : encodedList) {
            Job job = restoreJob(encoded);
            jobs.add(job);
        }
        return jobs;
    }

    @Override
    public int getTotalJob() {
        return broker.getList(queueKey).size();
    }

    @Override
    public boolean isEmpty() {
        return getAllJobIds().isEmpty();
    }

    @Override
    public AsyncResult enqueue(Task task) {
        JobOptions jobOptions = new JobOptions().setJobTimeout(this.defaultTimeout);
        return enqueue(task, jobOptions);
    }

    @Override
    public AsyncResult enqueue(Task task, JobOptions options) {
        JobImpl job = new JobImpl(this, task, options);
        broker.appendToList(queueKey, jobEncoder.writeAsBase64(job.dumps()));
        backend.updateStatus(job.getId(), JobStatus.QUEUED);
        return new AsyncResultImpl(backend, job);
    }

    @Override
    public Job nextJob(String worker) {
        String jobEncoded = broker.popFromList(queueKey);
        return restoreJob(jobEncoded);
    }

    @Override
    public void clear() {
        broker.removeList(queueKey);
    }

    private Job restoreJob(String encoded) {
        JobImpl job = new JobImpl(this);
        job.loads(jobEncoder.readFromBase64(encoded));
        return job;
    }
}
