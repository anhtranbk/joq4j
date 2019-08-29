package org.joq4j.backend;

import org.joq4j.Job;
import org.joq4j.JobExecutionException;
import org.joq4j.JobStatus;

import java.io.IOException;
import java.util.Map;

public class NullBackend implements StorageBackend {

    @Override
    public void storeJob(Job job) {
    }

    @Override
    public JobStatus getStatus(String jobId) {
        return null;
    }

    @Override
    public void updateStatus(String jobId, JobStatus status) {
    }

    @Override
    public Map<String, String> getMeta(String jobId) {
        return null;
    }

    @Override
    public void updateMeta(String jobId, Map<String, String> meta) {
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
