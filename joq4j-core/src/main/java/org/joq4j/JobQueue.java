package org.joq4j;

import java.util.List;

public interface JobQueue {

    String getName();

    QueueOptions getOptions();

    List<String> getAllJobIds();

    List<Job> getAllJobs();

    boolean isExists(String jobId);

    Job restoreJob(String jobId);

    int getTotalJob();

    boolean isEmpty();

    AsyncResult enqueue(AsyncTask task);

    AsyncResult enqueue(AsyncTask task, JobOptions options);

    AsyncResult enqueue(AsyncTask task, JobCallback callback);

    AsyncResult enqueue(AsyncTask task, JobOptions options, JobCallback callback);

    Job nextJob();

    Job cleanJob(String jobId);

    void clear();

    void delete(boolean deleteJobsFirst);
}
