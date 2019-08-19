package org.joq4j.internal;

import lombok.Getter;
import org.joq4j.AsyncResult;
import org.joq4j.AsyncTask;
import org.joq4j.Broker;
import org.joq4j.Job;
import org.joq4j.JobOptions;
import org.joq4j.JobQueue;
import org.joq4j.serde.Deserializer;
import org.joq4j.serde.SerdeFactory;
import org.joq4j.serde.Serializer;

import java.util.ArrayList;
import java.util.List;

@Getter
public class JobQueueImpl implements JobQueue {

    private static final String QUEUE_KEY_PREFIX = "jq:queue:";

    private final String name;
    private final String queueKey;
    private final Broker broker;

    private final long defaultTimeout;
    private final boolean async;
    private final Serializer serializer;
    private final Deserializer deserializer;

    public JobQueueImpl(String name, Broker broker, SerdeFactory serdeFactory,
                        long defaultTimeout, boolean async) {
        this.name = name;
        this.queueKey = QUEUE_KEY_PREFIX + this.name;
        this.defaultTimeout = defaultTimeout;
        this.async = async;
        this.broker = broker;
        this.serializer = serdeFactory.createSerializer();
        this.deserializer = serdeFactory.createDeserializer();
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public List<String> getAllJobIds() {
        return broker.getList(queueKey);
    }

    @Override
    public List<Job> getAllJobs() {
        List<String> jobIds = broker.getList(queueKey);
        List<Job> jobs = new ArrayList<>(jobIds.size());
        for (String jobId : jobIds) {
            jobs.add(restoreJob(jobId));
        }
        return jobs;
    }

    @Override
    public boolean isExists(String jobId) {
        return broker.get(JobImpl.generateJobKey(jobId)) != null;
    }

    @Override
    public Job restoreJob(String jobId) {
        JobImpl job = new JobImpl(this, null, new JobOptions());
        job.restoreFromBroker();
        return job;
    }

    @Override
    public int getTotalJob() {
        return getAllJobIds().size();
    }

    @Override
    public boolean isEmpty() {
        return getAllJobIds().isEmpty();
    }

    @Override
    public AsyncResult enqueue(AsyncTask task) {
        JobOptions jobOptions = new JobOptions().setJobTimeout(this.defaultTimeout);
        return enqueue(task, jobOptions);
    }

    @Override
    public AsyncResult enqueue(AsyncTask task, JobOptions options) {
        JobImpl job = new JobImpl(this, task, options);
        job.initNewJob();
        broker.appendToList(queueKey, job.getId());
        return new AsyncResultImpl(job);
    }

    @Override
    public Job nextJob(String worker) {
        String jobId = broker.popFromList(queueKey);
        return restoreJob(jobId);
    }

    @Override
    public Job cleanJob(String jobId) {
        return null;
    }

    @Override
    public void clear() {

    }

    @Override
    public void delete(boolean deleteJobsFirst) {

    }
}
