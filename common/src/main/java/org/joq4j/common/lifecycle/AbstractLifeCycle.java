package org.joq4j.common.lifecycle;

import org.joq4j.common.utils.Systems;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;

@SuppressWarnings("UnusedReturnValue")
public abstract class AbstractLifeCycle implements LifeCycle {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    private volatile int state = STOPPED;
    private final AtomicBoolean flagStop = new AtomicBoolean(false);

    public AbstractLifeCycle() {
        Systems.addShutdownHook(this::stop);
    }

    protected void onInitialize() {
    }

    protected void onStart() {
    }

    protected void onProcess() {
    }

    protected void onStop() {
    }

    public final boolean isCanceled() {
        return this.flagStop.get();
    }

    public final boolean isNotCanceled() {
        return !isCanceled();
    }

    @Override
    public final void start() {
        if (!this.setStarting()) return;

        logger.info("Life cycle initializing...");
        this.onInitialize();

        logger.info("Life cycle starting...");
        this.onStart();

        logger.info("Life cycle started...");
        this.setStarted();
        this.onProcess();
    }

    @Override
    public final void stop() {
        if (!this.setStopping()) return;

        logger.info("Life cycle stopping...");
        this.flagStop.set(true);
        this.onStop();

        logger.info("Life cycle stopped...");
        this.setStopped();
    }

    private synchronized boolean setStarting() {
        if (state == STARTING || state == STARTED) return false;
        this.state = STARTING;
        return true;
    }

    private synchronized boolean setStarted() {
        if (state == STARTING || state == STARTED) return false;
        this.state = STARTED;
        return true;
    }

    private synchronized boolean setStopping() {
        if (state == STOPPED || state == STOPPING) return false;
        this.state = STOPPING;
        return true;
    }

    private synchronized boolean setStopped() {
        if (state == STOPPED || state == STOPPING) return false;
        this.state = STOPPED;
        return true;
    }

    @Override
    public final int state() {
        return this.state;
    }

    @Override
    public final boolean isRunning() {
        return this.state == STARTING || this.state == STARTED;
    }

    @Override
    public final boolean isStarted() {
        return this.state == STARTED;
    }

    @Override
    public final boolean isStarting() {
        return this.state == STARTING;
    }

    @Override
    public final boolean isStopping() {
        return this.state == STOPPING;
    }

    @Override
    public final boolean isStopped() {
        return this.state == STOPPED;
    }
}
