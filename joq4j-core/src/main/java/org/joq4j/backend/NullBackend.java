package org.joq4j.backend;

import org.joq4j.Job;
import org.joq4j.JobExecutionException;
import org.joq4j.JobState;

import java.io.IOException;

/**
 * Default StorageBackend. Use if you want to ignore Job result
 */
public class NullBackend implements StorageBackend {

    @Override
    public void storeJob(Job job) {
    }

    @Override
    public Job fetchJob(String jobId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public JobState getState(String jobId) {
        return null;
    }

    @Override
    public void setState(String jobId, JobState status) {
    }

    @Override
    public void setWorker(String jobId, String worker) {
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
