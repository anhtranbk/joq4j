package org.joq4j.core;

import org.joq4j.AsyncResult;
import org.joq4j.AsyncTask;
import org.joq4j.Broker;
import org.joq4j.Job;
import org.joq4j.JobCallback;
import org.joq4j.JobOptions;
import org.joq4j.JobQueue;
import org.joq4j.JobStatus;
import org.joq4j.QueueOptions;

import java.util.ArrayList;
import java.util.List;

public class JobQueueImpl implements JobQueue {

    private static final String QUEUE_KEY_PREFIX = "jq:queue";
    private static final String JOB_KEY_PREFIX = "jq:job";

    private final String name;
    private final String queueKey;
    private final QueueOptions options;
    private final Broker broker;

    public JobQueueImpl(String name, QueueOptions options, Broker broker) {
        this.name = name;
        this.queueKey = QUEUE_KEY_PREFIX + ":" + this.name;
        this.options = options;
        this.broker = broker;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public QueueOptions getOptions() {
        return this.options;
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
        return !broker.get(JobImpl.generateJobKey(jobId)).isEmpty();
    }

    @Override
    public Job restoreJob(String jobId) {
        return null;
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
        JobOptions jobOptions = new JobOptions().setJobTimeout(this.options.getDefaultTimeout());
        return enqueue(task, jobOptions);
    }

    @Override
    public AsyncResult enqueue(AsyncTask task, JobOptions options) {
        return enqueue(task, options, null);
    }

    @Override
    public AsyncResult enqueue(AsyncTask task, JobCallback callback) {
        JobOptions jobOptions = new JobOptions().setJobTimeout(this.options.getDefaultTimeout());
        return enqueue(task, jobOptions, callback);
    }

    @Override
    public AsyncResult enqueue(AsyncTask task, JobOptions options, JobCallback callback) {
        JobImpl job = new JobImpl(task, this, this.broker, options, callback, options.getJobId());
        String data = this.options.getSerializer().serializeAsBase64(job);
        job.setStatus(JobStatus.QUEUED);
        return new AsyncResultImpl(job);
    }

    @Override
    public Job nextJob() {
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
