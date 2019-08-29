package org.joq4j.core;

import org.joq4j.AsyncResult;
import org.joq4j.Job;
import org.joq4j.JobCallback;
import org.joq4j.JobExecutionException;
import org.joq4j.JobStatus;
import org.joq4j.backend.StorageBackend;

import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

public class AsyncResultImpl implements AsyncResult {

    private final Job job;
    private final StorageBackend backend;

    AsyncResultImpl(StorageBackend backend, Job job) {
        this.job = job;
        this.backend = backend;
    }

    @Override
    public boolean cancel(boolean b) {
        try {
            backend.updateStatus(job.getId(), JobStatus.CANCELLED);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    @Override
    public boolean isCancelled() {
        return backend.getStatus(job.getId()).equals(JobStatus.CANCELLED);
    }

    @Override
    public boolean isDone() {
        return backend.isJobDone(job.getId());
    }

    @Override
    public Object get() {
        return backend.waitForJobResult(job.getId(), 0L);
    }

    @Override
    public Object get(long l, TimeUnit timeUnit) {
        return backend.waitForJobResult(job.getId(), timeUnit.toMillis(l));
    }

    @Override
    public void addCallback(JobCallback callback) {
        addCallback(callback, ForkJoinPool.commonPool());
    }

    @Override
    public void addCallback(JobCallback callback, Executor executor) {
        throw new UnsupportedOperationException();
    }
}
