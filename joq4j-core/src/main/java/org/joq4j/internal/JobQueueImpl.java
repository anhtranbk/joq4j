package org.joq4j.internal;

import lombok.Getter;
import org.joq4j.AsyncResult;
import org.joq4j.AsyncTask;
import org.joq4j.Broker;
import org.joq4j.Job;
import org.joq4j.JobOptions;
import org.joq4j.JobQueue;
import org.joq4j.common.utils.Utils;
import org.joq4j.serde.Deserializer;
import org.joq4j.serde.SerdeFactory;
import org.joq4j.serde.Serializer;

import java.util.ArrayList;
import java.util.Collections;
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
        List<String> jobIds = broker.getList(queueKey);
        if (Utils.isEmpty(jobIds)) return Collections.emptyList();

        List<String> aliveJobIds = new ArrayList<>(jobIds.size());
        for (String jobId : jobIds) {
            if (!JobImpl.checkAlive(this, jobId)) continue;
            aliveJobIds.add(jobId);
        }
        return aliveJobIds;
    }

    @Override
    public List<Job> getAllJobs() {
        List<String> jobIds = getAllJobIds();
        if (jobIds.isEmpty()) return Collections.emptyList();

        List<Job> jobs = new ArrayList<>(jobIds.size());
        for (String jobId : jobIds) {
            jobs.add(restoreJob(jobId));
        }
        return jobs;
    }

    @Override
    public int getTotalJob() {
        return getAllJobIds().size();
    }

    @Override
    public boolean isExists(String jobId) {
        return JobImpl.checkExists(this, jobId);
    }

    @Override
    public Job restoreJob(String jobId) {
        return isExists(jobId) ? JobImpl.fetch(this, jobId) : null;
    }

    @Override
    public boolean isEmpty() {
        return Utils.isEmpty(getAllJobIds());
    }

    @Override
    public AsyncResult enqueue(AsyncTask task) {
        JobOptions jobOptions = new JobOptions().setJobTimeout(this.defaultTimeout);
        return enqueue(task, jobOptions);
    }

    @Override
    public AsyncResult enqueue(AsyncTask task, JobOptions options) {
        String jobId = options.getJobId();
        if (jobId != null && isExists(jobId)) {
            throw new IllegalStateException("Job ID " + jobId + " already exists in queue");
        }
        JobImpl job = new JobImpl(this, task, options);
        job.initNewJob();
        broker.appendToList(queueKey, job.getId());
        return new AsyncResultImpl(job);
    }

    @Override
    public Job nextJob(String worker) {
        while (true) {
            String jobId = broker.popFromList(queueKey);
            if (jobId == null) {
                return null;
            }
            if (!JobImpl.checkAlive(this, jobId)) continue;
            return restoreJob(jobId);
        }
    }

    @Override
    public Job removeJob(String jobId) {
        JobImpl job = JobImpl.fetch(this, jobId);
        if (job != null) {
            job.delete();
        }
        return job;
    }

    @Override
    public void clear() {
        getAllJobIds().forEach(this::removeJob);
        broker.removeList(queueKey);
    }
}
