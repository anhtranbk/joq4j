package org.joq4j.core;

import org.joq4j.AsyncResult;
import org.joq4j.JobCallback;

import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class AsyncResultImpl implements AsyncResult {

    private final JobImpl job;

    AsyncResultImpl(JobImpl job) {
        this.job = job;
    }

    @Override
    public boolean cancel(boolean b) {
        return job.cancel();
    }

    @Override
    public boolean isCancelled() {
        return this.job.isCancelled();
    }

    @Override
    public boolean isDone() {
        return this.job.isDone();
    }

    @Override
    public Object get() {
        try {
            return job.waitForJobResult(0L);
        } catch (TimeoutException ignored) {
            // Unreachable code cause
        }
        return null;
    }

    @Override
    public Object get(long l, TimeUnit timeUnit) throws TimeoutException {
        return job.waitForJobResult(timeUnit.toMillis(l));
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
