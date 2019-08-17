package org.joq4j.common.lifecycle;

/**
 * Đại diện cho vòng đời của một đối tượng qua các giai đoạn:
 * INIT -> STARTING -> STARTED -> STOPPING -> STOPPED
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public interface LifeCycle {

    int STARTING = 1;
    int STARTED = 2;
    int STOPPING = 3;
    int STOPPED = -1;

    /**
     * Start đối tượng, chuyển sang các trạng thái starting -> started
     */
    void start();

    /**
     * Stop đối tượng, chuyển sang các trạng thái stopping -> stopped
     */
    void stop();

    /**
     * @return Trạng thái hiện tại
     */
    int state();

    boolean isRunning();

    boolean isStarted();

    boolean isStarting();

    boolean isStopping();

    boolean isStopped();
}
