package org.joq4j;

import lombok.Setter;
import lombok.experimental.Accessors;
import org.joq4j.backend.BackendFactory;
import org.joq4j.backend.BackendFactoryRegistry;
import org.joq4j.backend.NullBackend;
import org.joq4j.backend.StorageBackend;
import org.joq4j.broker.Broker;
import org.joq4j.broker.BrokerFactory;
import org.joq4j.broker.BrokerFactoryRegistry;
import org.joq4j.broker.MemoryBroker;
import org.joq4j.config.Config;
import org.joq4j.impl.JobQueueImpl;

import java.util.List;

public interface JobQueue {

    String name();

    QueueOptions options();

    Broker broker();

    StorageBackend backend();

    AsyncResult enqueue(Task task, String name);

    AsyncResult enqueue(Task task);

    AsyncResult enqueue(Task task, JobCallback callback);

    AsyncResult enqueue(Job job);

    Job pop(String worker, long timeout);

    default List<Job> getQueueJobs() {
        throw new UnsupportedOperationException();
    }

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
        private Broker broker = new MemoryBroker();
        private StorageBackend backend = new NullBackend();
        private Config config = new Config();

        public Builder broker(Broker broker) {
            this.broker = broker;
            return this;
        }

        public Builder broker(String brokerUrl) {
            BrokerFactory factory = BrokerFactoryRegistry.findFactory(brokerUrl);
            this.broker = factory.createBroker(config, brokerUrl);
            return this;
        }

        public Builder backend(StorageBackend backend) {
            this.backend = backend;
            return this;
        }

        public Builder backend(String backendUrl) {
            BackendFactory factory = BackendFactoryRegistry.findFactory(backendUrl);
            this.backend = factory.createBackend(config, backendUrl);
            return this;
        }

        public JobQueue build() {
            return new JobQueueImpl(name, broker, backend, config);
        }
    }

    static Builder builder() {
        return new Builder();
    }
}
