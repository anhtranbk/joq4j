package org.joq4j;

import lombok.Setter;
import lombok.experimental.Accessors;
import org.joq4j.backend.NullBackend;
import org.joq4j.backend.StorageBackend;
import org.joq4j.broker.MemoryBroker;
import org.joq4j.core.JobQueueImpl;
import org.joq4j.encoding.Encoder;
import org.joq4j.encoding.JavaSerializer;
import org.joq4j.encoding.JsonEncoder;
import org.joq4j.encoding.Serializer;

import java.util.List;

public interface JobQueue {

    String getName();

    List<String> getAllJobIds();

    List<Job> getAllJobs();

    int getTotalJob();

    boolean isEmpty();

    AsyncResult enqueue(Task task);

    AsyncResult enqueue(Task task, JobOptions options);

    Job nextJob(String worker);

    void clear();

    @Accessors(fluent = true)
    @Setter
    class Builder {
        private String name = "default";
        private Encoder jobEncoder = new JsonEncoder();
        private Serializer serializer = new JavaSerializer();
        private long defaultTimeout = 600L;
        private Broker broker = new MemoryBroker();
        private StorageBackend backend = new NullBackend();

        public JobQueue build() {
            return new JobQueueImpl(
                    name, broker, backend,
                    jobEncoder, serializer, defaultTimeout);
        }
    }

    static Builder builder() {
        return new Builder();
    }
}
