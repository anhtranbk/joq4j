package org.joq4j.backend;

import org.joq4j.Job;
import org.joq4j.JobCallback;
import org.joq4j.JobExecutionException;
import org.joq4j.JobStatus;
import org.joq4j.common.utils.Threads;
import org.joq4j.encoding.Serializer;

import java.io.Closeable;
import java.util.Map;
import java.util.concurrent.Executor;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public interface StorageBackend extends Closeable {

    default Serializer getSerializer() {
        return null;
    }

    void storeJob(Job job);

    JobStatus getStatus(String jobId);

    void updateStatus(String jobId, JobStatus status);

    Map<String, String> getMeta(String jobId);

    void updateMeta(String jobId, Map<String, String> meta);

    void markAsSuccess(String jobId, Object result);

    void markAsFailure(String jobId, Throwable error);

    JobExecutionException getError(String jobId);

    Object getResult(String jobId);

    default void waitForJobResult(JobCallback callback, Executor executor) {
        throw new UnsupportedOperationException();
    }

    default Object waitForJobResult(String jobId, long timeout) {
        long now;
        do {
            if (isJobDone(jobId)) {
                Object result = getResult(jobId);
                if (result == null) {
                    throw getError(jobId);
                }
                return result;
            }
            Threads.sleep(1);
            now = System.currentTimeMillis();
        } while (System.currentTimeMillis() - now > timeout || timeout == 0);
        throw new JobExecutionException("Job timeout");
    }

    default boolean isJobDone(String jobId) {
        JobStatus status = getStatus(jobId);
        return status.equals(JobStatus.SUCCESS) || status.equals(JobStatus.FAILURE);
    }

    default void ensureJobFinished(String jobId) {
        if (!isJobDone(jobId)) {
            throw new IllegalStateException("Job is not finished " + jobId);
        }
    }
}
