package org.joq4j.backend;

import org.joq4j.Job;
import org.joq4j.JobExecutionException;
import org.joq4j.JobState;
import org.joq4j.encoding.TaskSerializer;

import java.io.IOException;

public class NullBackend implements StorageBackend {

    @Override
    public TaskSerializer getTaskSerializer() {
        return null;
    }

    @Override
    public void storeJob(Job job) {
    }

    @Override
    public Job getJob(String jobId) {
        return null;
    }

    @Override
    public JobState getState(String jobId) {
        return null;
    }

    @Override
    public void setState(String jobId, JobState status) {
    }

    @Override
    public void markAsSuccess(String jobId, Object result) {
    }

    @Override
    public void markAsFailure(String jobId, Throwable error) {
    }

    @Override
    public JobExecutionException getError(String jobId) {
        return null;
    }

    @Override
    public Object getResult(String jobId) {
        return null;
    }

    @Override
    public void close() throws IOException {
    }
}
