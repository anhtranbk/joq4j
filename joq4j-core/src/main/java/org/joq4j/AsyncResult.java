package org.joq4j;

import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public interface AsyncResult extends Future<Object> {

    Object get();

    Object get(long l, TimeUnit timeUnit);

    void addCallback(JobCallback callback);

    void addCallback(JobCallback callback, Executor executor);
}
