package org.joq4j.common.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public class Threads {

    public static void stopThreadPool(ExecutorService executor) {
        executor.shutdown();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void stopThreadPool(ExecutorService executor, long timeout, TimeUnit unit) {
        try {
            executor.shutdown();
            executor.awaitTermination(timeout, unit);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void sleep(long duration, TimeUnit unit) {
        sleep(unit.toMillis(duration));
    }

    public static void sleepSeconds(long seconds) {
        Threads.sleep(seconds * 1000);
    }

    public static String wrapWithThreadInfo(String msg) {
        return "[" + Thread.currentThread().getName() + "-" + Thread.currentThread().getId() + "] " + msg;
    }

    public static String threadName() {
        return Thread.currentThread().getName();
    }

    public static String threadInfo() {
        return Strings.format("[id=%s, name=%s]",
                Thread.currentThread().getId(), Thread.currentThread().getName());
    }
}
