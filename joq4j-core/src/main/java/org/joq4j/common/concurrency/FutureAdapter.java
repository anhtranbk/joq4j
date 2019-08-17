package org.joq4j.common.concurrency;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public class FutureAdapter<S, R> implements Future<R> {

    private final Future<S> src;
    private final Converter<S, R> converter;
    private final AtomicReference<R> result = new AtomicReference<>();

    /**
     *
     * @param src
     * @param converter
     */
    public FutureAdapter(Future<S> src, Converter<S, R> converter) {
        this.src = src;
        this.converter = converter;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return src.cancel(mayInterruptIfRunning);
    }

    @Override
    public boolean isCancelled() {
        return src.isCancelled();
    }

    @Override
    public boolean isDone() {
        return src.isDone();
    }

    @Override
    public R get() throws InterruptedException, ExecutionException {
        if (result.get() == null) {
            result.compareAndSet(null, converter.convert(src.get()));
        }
        return result.get();
    }

    @Override
    public R get(long timeout, TimeUnit unit)
            throws InterruptedException, ExecutionException, TimeoutException {
        return converter.convert(src.get(timeout, unit));
    }

//    @Override
//    public void addListener(Runnable runnable, Executor executor) {
//        if (src instanceof ListenableFuture) {
//            ((ListenableFuture) src).addListener(runnable, executor);
//        } else {
//            executor.perform(() -> {
//                try {
//                    get();
//                    runnable.run();
//                } catch (Exception ignored) {
//                }
//            });
//        }
//    }

    /**
     *
     * @param src
     * @param converter
     * @param <S>
     * @param <R>
     * @return
     */
    public static <S, R> FutureAdapter<S, R> from(Future<S> src, Converter<S, R> converter) {
        return new FutureAdapter<>(src, converter);
    }
}
