package org.joq4j;

import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(chain = true)
public @Data class JobOptions {

    /**
     * Custom jobId, if not specified, an unique random ID will be generated
     */
    private String jobId;

    /**
     * Additional description to enqueued jobs.
     */
    private String description;
    /**
     * Specifies the maximum runtime of the job before it’s interrupted and marked
     * as failed. Its default unit is second and it can be a string representing an
     * integer(e.g. 2, '2'). Furthermore, it can be a string with specify unit
     * including hour, minute, second(e.g. '1h', '3m', '5s').
     */
    private long jobTimeout;
    /**
     * Specifies how long (in seconds) successful jobs and their results are kept.
     * Expired jobs will be automatically deleted. Defaults to 500 seconds.
     */
    private long resultTtl;
    /**
     * Specifies how long failed jobs are kept (defaults to 1 year)
     */
    private long failureTtl;
    /**
     * Specifies the maximum queued time of the job before it’s discarded. If you
     * specify a value of -1 you indicate an infinite job ttl and it will run indefinitely
     */
    private long ttl;

    private JobPriority jobPriority;
}
