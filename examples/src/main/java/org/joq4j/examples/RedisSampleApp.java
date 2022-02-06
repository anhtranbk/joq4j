package org.joq4j.examples;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.joq4j.AsyncResult;
import org.joq4j.Job;
import org.joq4j.JobBuilder;
import org.joq4j.JobQueue;
import org.joq4j.backend.StorageBackend;
import org.joq4j.common.utils.Threads;
import org.joq4j.redis.backend.RedisBackend;
import org.joq4j.redis.broker.RedisBroker;
import org.joq4j.serde.JacksonMessageEncoder;
import org.joq4j.serde.MessageEncoder;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class RedisSampleApp {

    public static void main(String[] args) throws Exception {
        Class.forName("org.joq4j.redis.broker.RedisBroker");
        Class.forName("org.joq4j.redis.backend.RedisBackend");

        String redisUrl = "redis://localhost:6379/0";
        JobQueue queue = JobQueue.builder()
                .broker(new RedisBroker(redisUrl))
                .backend(new RedisBackend(redisUrl))
                .build();

        loopProduceConsumeJob(queue);
//        executeJobAndGetResult(queue);
//        testCustomSerializer();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    static void loopProduceConsumeJob(JobQueue queue) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        executor.execute(() -> {
            Random random = new Random();
            for (int i = 0; i < 100; i++) {
                Threads.sleepSeconds(2);
                final int a = random.nextInt(100);
                final int b = random.nextInt(100);
                queue.enqueue(() -> a + b);
            }
        });
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.HOURS);
    }

    static void executeJobAndGetResult(JobQueue queue) throws Exception {
        StorageBackend backend = queue.backend();
        AsyncResult result = queue.enqueue(() -> "Hello, World!");

        String jobId = result.getJobId();
        Job job = backend.fetchJob(jobId);

        ObjectMapper om = JacksonMessageEncoder.currentObjectMapper();
        om.enable(SerializationFeature.INDENT_OUTPUT);
        System.out.println("Job fetched\n" + om.writeValueAsString(job));

        while (!result.isDone()) {
            Threads.sleepSeconds(2);
            System.out.println("Job State:" + backend.getState(jobId));
        }
        System.out.println(result.get());
    }

    static void testCustomSerializer() {
        Job job = Job.builder()
                .task(() -> "Hello, sieunhan!")
                .name("test custom serializer")
                .id(UUID.randomUUID().toString())
                .build();
        MessageEncoder encoder = new JacksonMessageEncoder();
        String encoded = encoder.writeAsString(job);
        System.out.println(encoded);

        try {
            job = encoder.read(encoded, JobBuilder.class).build();
            Object result = job.task().call();
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
