package org.joq4j;

import lombok.Data;
import lombok.experimental.Accessors;
import org.joq4j.config.ConfigDescriptor;
import org.joq4j.config.Configurable;

import java.util.concurrent.TimeUnit;

@Accessors(chain = true, fluent = true)
public @Data class TaskOptions implements Configurable {

    /**
     * Custom job name, default is empty
     */
    private String name = "";

    /**
     * Additional description to enqueued jobs.
     */
    private String description = "";

    /**
     * Specifies the maximum runtime of the job before it’s interrupted and marked
     * as failed in seconds.
     */
    private long timeout = 600;

    private int maxRetries = 3;

    private long retryDelay = 180;

    private int priority = 1;

    @ConfigDescriptor(name = "joq4j.worker.storeResult")
    private boolean storeResult = true;

    @ConfigDescriptor(name = "joq4j.worker.storeErrorEventIfIgnored")
    private boolean storeErrorEventIfIgnored = true;

    @ConfigDescriptor(name = "joq4j.worker.sentEvents")
    private boolean sentEvents = false;

    /**
     * Specifies the maximum runtime of the job before it’s interrupted and marked
     * as failed. Its default unit is second and it can be a string representing an
     * integer(e.g. 2, '2'). Furthermore, it can be a string with specify unit
     * including hour, minute, second(e.g. '1h', '3m', '5s').
     *
     * @param timeout Job timeout represent in string
     */
    public TaskOptions setTimeout(String timeout) {
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
}
