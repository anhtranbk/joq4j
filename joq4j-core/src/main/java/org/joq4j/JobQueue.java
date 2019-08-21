package org.joq4j;

import lombok.Setter;
import lombok.experimental.Accessors;
import org.joq4j.broker.MemoryBroker;
import org.joq4j.internal.JobQueueImpl;
import org.joq4j.serde.JavaSerdeFactory;
import org.joq4j.serde.SerdeFactory;

import java.util.List;

public interface JobQueue {

    String getName();

    List<String> getAllJobIds();

    List<Job> getAllJobs();

    boolean isExists(String jobId);

    Job restoreJob(String jobId);

    int getTotalJob();

    boolean isEmpty();

    AsyncResult enqueue(AsyncTask task);

    AsyncResult enqueue(AsyncTask task, JobOptions options);

    Job nextJob(String worker);

    Job removeJob(String jobId);

    void clear();

    @Accessors(fluent = true)
    @Setter
    class Builder {
        private String name = "default";
        private SerdeFactory serializationFactory = new JavaSerdeFactory();
        private long defaultTimeout = 600L;
        private boolean async = true;
        private Broker broker = new MemoryBroker();

        public JobQueue build() {
            return new JobQueueImpl(name, broker, serializationFactory, defaultTimeout, async);
        }
    }

    static Builder builder() {
        return new Builder();
    }
}
