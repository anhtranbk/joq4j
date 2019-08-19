package org.joq4j;

import java.util.concurrent.Executor;
import java.util.concurrent.Future;

public interface AsyncResult extends Future<Object> {

    void addCallback(JobCallback callback);

    void addCallback(JobCallback callback, Executor executor);
}
