package org.joq4j.common.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Contains helper methods
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public class Utils {

    @SuppressWarnings("unchecked")
    public static <T> int compare(Comparable<T> c1, Comparable<T> c2) {
        return c1.compareTo((T) c2);
    }

    public static <T> T lastItem(Iterable<T> collection) {
        if (collection instanceof List) {
            List<T> list = (List<T>) collection;
            return list.get(list.size() - 1);
        }
        Iterator<T> iterator = collection.iterator();
        T last = iterator.next();
        while (iterator.hasNext()) last = iterator.next();
        return last;
    }

    public static boolean isEmpty(Object o) {
        if (o == null) {
            return true;
        } else if (o instanceof CharSequence) {
            return ((CharSequence) o).length() == 0;
        } else if (o.getClass().isArray()) {
            return ((Object[]) ((Object[]) o)).length == 0;
        } else if (o instanceof Collection) {
            return ((Collection) o).isEmpty();
        } else {
            return o instanceof Map ? ((Map) o).isEmpty() : false;
        }
    }

    public static void systemExit(String message) {
        System.out.println(message);
        System.exit(0);
    }

    public static void systemError(String message) {
        System.err.println(message);
        System.exit(1);
    }

    public static void systemError(Throwable throwable) {
        throwable.printStackTrace();
        System.exit(1);
    }

    public static String wrapWithThreadInfo(String msg) {
        return "[" + Thread.currentThread().getName() + "-" + Thread.currentThread().getId() + "] " + msg;
    }

    public static String currentThreadName() {
        return "[" + Thread.currentThread().getName() + "] ";
    }

    public static long reverseTimestamp() {
        return Long.MAX_VALUE - System.currentTimeMillis();
    }

    public static void addShutdownHook(Runnable target) {
        Runtime.getRuntime().addShutdownHook(new Thread(target));
    }

    public static <E> boolean notEquals(E e1, E e2) {
        return !e1.equals(e2);
    }

    public static String getHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            return "Unknown";
        }
    }
}
