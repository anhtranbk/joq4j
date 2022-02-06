package org.joq4j.examples;

import org.joq4j.Job;
import org.joq4j.JobQueue;
import org.joq4j.common.utils.Threads;
import org.joq4j.redis.backend.RedisBackend;
import org.joq4j.redis.broker.RedisBroker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleWorker {

    static final Logger logger = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);

    public static void main(String[] args) throws Exception {
        String redisUrl = "redis://localhost:6379/0";
        JobQueue queue = JobQueue.builder()
                .broker(new RedisBroker(redisUrl))
                .backend(new RedisBackend(redisUrl))
                .build();

        //noinspection InfiniteLoopStatement
        while (true) {
            Job job = queue.pop("simple", 100);
            if (job != null) {
                logger.info("Received new job: " + job);
                Object result = job.task().call();
                logger.info("Processed job: " + job.id() + ". Result: " + result);
            } else Threads.sleepSeconds(1);
        }
    }
}
