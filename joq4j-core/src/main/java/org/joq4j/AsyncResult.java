package org.joq4j;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public interface AsyncResult extends Future<Object> {

    String getJobId();

    Object get();

    Object get(long l, @NotNull TimeUnit timeUnit);

    void addCallback(JobCallback callback);

    void addCallback(JobCallback callback, Executor executor);
}
