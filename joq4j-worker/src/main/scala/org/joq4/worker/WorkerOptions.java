package org.joq4.worker;

import lombok.Data;
import org.joq4j.config.ConfigDescriptor;
import org.joq4j.config.Configurable;

@Data
@Configurable
// Almost options inspired-by Celery worker
public class WorkerOptions {
    // Global options
    private String name;

    private String brokerUrl;

    private String backendUrl;

    private String configUri;

    private String workDir;

    private String logLevel;

    // Worker options
    private int concurrency;

    private String autoScale;

    @ConfigDescriptor(name = "worker.workerFactoryClass")
    private String workerFactoryClass;

    private boolean sendTaskEvents;

    private long taskTimeLimit;

    private int maxTasksPerExecutor;

    // Queue options
    private String queues;

    private String excludeQueues;

    private boolean discardAllWaitingTasks;

    // Other features
    @ConfigDescriptor(name = "withScheduler")
    private boolean runWithScheduler;

    @ConfigDescriptor(name = "worker.schedulerClass")
    private String schedulerClass;

    @ConfigDescriptor(name = "worker.withoutHeartbeat")
    private boolean disableHeartbeat;

    @ConfigDescriptor(name = "worker.heartbeatIntervalMillis")
    private int heartbeatIntervalMillis;

    // Redis-queue specified options

    private int maxJobs;

    private long resultTtl;

    // Joq4j specified options

    @ConfigDescriptor(name = "worker.brokerFactoryClass")
    private String brokerFactoryClass;

    @ConfigDescriptor(name = "worker.backendFactoryClass")
    private String backendFactoryClass;

    private String javaSerializerClass;

    private String messageEncoderClass;
}
