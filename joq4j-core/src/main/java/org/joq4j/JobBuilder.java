package org.joq4j;

import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Accessors(chain = true, fluent = true)
@Setter
public class JobBuilder {

    public static final int JOB_PRIORITY_NORMAL = 1;

    private static final int DEFAULT_MAX_RETRIES = 3;
    private static final long DEFAULT_RETRY_DELAY = 1000;
    private static final int DEFAULT_PRIORITY = JOB_PRIORITY_NORMAL;

    private String id;
    private String groupId = "";
    private String name = "";
    private String description = "";
    private long eta = 0;
    private long timeout = 0;
    private int maxRetries = DEFAULT_MAX_RETRIES;
    private long retryDelay = DEFAULT_RETRY_DELAY;
    private int priority = DEFAULT_PRIORITY;
    private Map<String, String> meta = new HashMap<>();
    private Task task;
    private JobCallback jobCallback;

    public JobBuilder addMeta(String key, String value) {
        this.meta.put(key, value);
        return this;
    }

    public JobBuilder timeout(long timeout) {
        this.timeout = timeout;
        return this;
    }

    /**
     * Specifies the maximum runtime of the job before it’s interrupted and marked
     * as failed. Its default unit is second, and it can be a string representing an
     * integer(e.g. 2, '2'). Furthermore, it can be a string with specify unit
     * including hour, minute, second(e.g. '1h', '3m', '5s').
     *
     * @param timeout Job timeout represent in string
     */
    public JobBuilder timeout(String timeout) {
        try {
            this.timeout = Long.parseLong(timeout);
        } catch (NumberFormatException e) {
            long amount = Long.parseLong(timeout.substring(0, timeout.length() - 1));
            String unit = timeout.substring(timeout.length() - 1);
            switch (unit) {
                case "s":
                    this.timeout = amount;
                    break;
                case "m":
                    this.timeout = TimeUnit.MINUTES.toSeconds(amount);
                    break;
                case "h":
                    this.timeout = TimeUnit.HOURS.toSeconds(amount);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid unit time or bad format");
            }
        }
        return this;
    }

    public Job build() {
        return new Job(
                id, groupId, name, description,
                eta, timeout, maxRetries, retryDelay, priority,
                task, jobCallback, meta
        );
    }
}
