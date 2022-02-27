package org.joq4j.common.lifecycle;

public interface LifeCycle {

    int STARTING = 1;
    int STARTED = 2;
    int STOPPING = 3;
    int STOPPED = -1;

    void start();

    void stop();

    int state();

    boolean isRunning();

    boolean isStarted();

    boolean isStarting();

    boolean isStopping();

    boolean isStopped();
}
