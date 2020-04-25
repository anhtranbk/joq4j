package org.joq4j;

import lombok.Setter;
import lombok.experimental.Accessors;
import org.joq4j.backend.NullBackend;
import org.joq4j.backend.StorageBackend;
import org.joq4j.broker.Broker;
import org.joq4j.broker.MemoryBroker;
import org.joq4j.core.JobQueueImpl;
import org.joq4j.encoding.Encoder;
import org.joq4j.encoding.JacksonEncoder;
import org.joq4j.encoding.JavaTaskSerializer;
import org.joq4j.encoding.TaskSerializer;

import java.util.List;

public interface JobQueue {

    String getName();

    AsyncResult enqueue(Task task);

    AsyncResult enqueue(Task task, TaskOptions options);

    Job nextJob(String worker);

    default List<Job> getPendingJobs() {
        throw new UnsupportedOperationException();
    }

    default int getTotalJob() {
        throw new UnsupportedOperationException();
    }

    default boolean isEmpty() {
        throw new UnsupportedOperationException();
    }

    default void clear() {
        throw new UnsupportedOperationException();
    }

    @Accessors(fluent = true)
    @Setter
    class Builder {
        private String name = "default";
        private Encoder jobEncoder = new JacksonEncoder();
        private TaskSerializer taskSerializer = new JavaTaskSerializer();
        private long defaultTimeout = 600L;
        private Broker broker = new MemoryBroker();
        private StorageBackend backend = new NullBackend();

        public JobQueue build() {
            return new JobQueueImpl(
                    name, broker, backend,
                    jobEncoder, taskSerializer, defaultTimeout);
        }
    }

    static Builder builder() {
        return new Builder();
    }
}
