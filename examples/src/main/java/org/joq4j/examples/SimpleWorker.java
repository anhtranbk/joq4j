package org.joq4j.examples;

import org.joq4j.Job;
import org.joq4j.JobQueue;
import org.joq4j.common.utils.Threads;
import org.joq4j.redis.jedis.RedisBackendFactory;
import org.joq4j.redis.jedis.RedisBrokerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleWorker {

    static final Logger logger = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);

    public static void main(String[] args) throws Exception {
        Class.forName(RedisBrokerFactory.class.getName());
        Class.forName(RedisBackendFactory.class.getName());

        String redisUrl = "redis://localhost:6379/0";
        JobQueue queue = JobQueue.builder()
                .broker(redisUrl)
                .backend(redisUrl)
                .build();

        int counter = 0;
        while (counter < 60) {
            Job job = queue.pop("simple", 100);
            if (job != null) {
                counter = 0;
                logger.info("Received new job: " + job);
                Object result = job.task().call();
                logger.info("Processed job: " + job.id() + ". Result: " + result);
            } else {
                Threads.sleepSeconds(1);
                counter++;
            }
        }
        System.out.println("Wait too long for new job. Exiting...");
    }
}
