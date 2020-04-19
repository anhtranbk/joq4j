package org.joq4j.common.lifecycle;

import org.joq4j.common.utils.Systems;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public abstract class AbstractLifeCycle implements LifeCycle {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    private volatile int state = STOPPED;
    private final Object _lock = new Object();
    private final AtomicBoolean flagStop = new AtomicBoolean(false);

    public AbstractLifeCycle() {
        Systems.addShutdownHook(AbstractLifeCycle.this::stop);
    }

    /**
     * Class con kế thừa nên implement các cài đặt, khởi tạo cho đối tương ở đây.
     * Giai đoạn này sẽ được gọi trước khi đối tượng được start.
     */
    protected void onInitialize() {
    }

    /**
     * Các xử lý trong quá trình start của đối tượng, ngay sau khi quá trình init
     * kết thúc. Kết thúc hàm này đối tượng được coi là started.
     */
    protected void onStart() {
    }

    /**
     * Các xử lý chính của đối tượng sau khi started, đây là sự mở rộng của
     * LifeCycle chuẩn và đối tượng vẫn có trạng thái là started.
     */
    protected void onProcess() {
    }

    /**
     * Các xử lý khi đối tượng chuẩn bị stop. Sau khi hàm này kết thúc đối
     * tượng được coi là stopped.
     */
    protected void onStop() {
    }

    /**
     * @return true nếu quá trình xử lý của đối tượng bị cancel bởi user
     * và ngược lại
     */
    public final boolean isCanceled() {
        return this.flagStop.get();
    }

    /**
     * @return true nếu quá trình xử lý của đối tượng không bị cancel
     * bởi user và ngược lại
     */
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

    private boolean setStarting() {
        synchronized (_lock) {
            if (state == STARTING || state == STARTED) return false;
            this.state = STARTING;
        }
        return true;
    }

    private boolean setStarted() {
        synchronized (_lock) {
            if (state == STARTING || state == STARTED) return false;
            this.state = STARTED;
        }
        return true;
    }

    private boolean setStopping() {
        synchronized (_lock) {
            if (state == STOPPED || state == STOPPING) return false;
            this.state = STOPPING;
        }
        return true;
    }

    private boolean setStopped() {
        synchronized (_lock) {
            if (state == STOPPED || state == STOPPING) return false;
            this.state = STOPPED;
        }
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
