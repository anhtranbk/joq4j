package org.joq4j;

import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.joq4j.common.utils.Strings;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

@Accessors(chain = true, fluent = true)
@Getter
public class Job {

    private final String id;

    private final String groupId;

    /**
     * Custom job name, default is empty
     */
    private final String name;

    /**
     * Additional description to enqueued jobs.
     */
    private final String description;

    private final long eta;

    /**
     * Specifies the maximum runtime of the job before it’s interrupted and marked
     * as failed in seconds.
     */
    private final long timeout;

    private final int maxRetries;

    private final long retryDelay;

    private final int priority;

    private final Map<String, String> meta;

    private final Task task;

    private final JobCallback jobCallback;

    public Job(String id, String groupId, String name, String description,
               long eta, long timeout, int maxRetries, long retryDelay, int priority,
               Task task, JobCallback jobCallback, Map<String, String> meta)
    {
        if (Strings.isNullOrWhitespace(id)) {
            id = UUID.randomUUID().toString();
        }
        if (Strings.isNullOrWhitespace(groupId)) {
            groupId = id;
        }
        Preconditions.checkNotNull(task, "Task must not be null");

        this.id = id;
        this.groupId = groupId;
        this.name = name;
        this.description = description;
        this.eta = eta;
        this.timeout = timeout;
        this.maxRetries = maxRetries;
        this.retryDelay = retryDelay;
        this.priority = priority;
        this.task = task;
        this.jobCallback = jobCallback;
        this.meta = Collections.unmodifiableMap(meta);
    }

    public static JobBuilder builder() {
        return new JobBuilder();
    }
}
