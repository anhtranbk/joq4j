package org.joq4j.common.lifecycle;

import org.joq4j.common.utils.Threads;

import java.util.concurrent.TimeUnit;

/**
 * LifeCycle có khả năng tự động repeat lại quá trình xử lý nếu
 * đã hoàn thành hoặc có lỗi xảy ra.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public abstract class LoopableLifeCycle extends AbstractLifeCycle {

    private long sleepAfterDone;
    private long sleepAfterFail;

    public LoopableLifeCycle(long sleepAfterDone, long sleepAfterFail) {
        setSleepTime(sleepAfterDone, sleepAfterFail);
    }

    public LoopableLifeCycle() {
        setSleepTime(60, 30);
    }

    @Override
    protected final void onProcess() {
        boolean success;
        while (!isCanceled()) {
            try {
                this.onLoop();
                success = true;
            } catch (Throwable t) {
                success = false;
                this.logger.error(t.getMessage(), t);
            }

            Threads.sleep(success ? sleepAfterDone : sleepAfterFail, TimeUnit.SECONDS);
        }
    }

    /**
     * Thiết lập khoảng thời gian nghỉ giữa các lần xử lý
     *
     * @param sleepAfterDone thời gian nghỉ giữa các lần xử lý thành công
     * @param sleepAfterFail thời gian nghỉ nếu lần cuối cùng xử lý thất bại
     */
    protected void setSleepTime(long sleepAfterDone, long sleepAfterFail) {
        this.sleepAfterDone = sleepAfterDone;
        this.sleepAfterFail = sleepAfterFail;
    }

    /**
     * Nơi chứa các xử lý chính sẽ được lặp đi lặp lại. Mỗi khi hàm này kết
     * thúc một cách thành công (không có lỗi) sẽ được coi là một lần xử lý
     * thành công và đối tượng sẽ lặp lại các xử lý này sau một khoảng thời
     * gian xác định bởi <i>sleepAfterDone</i>. Ngược lại nếu có lỗi xảy ra
     * đối tượng sẽ repeat các xử lý sau khoảng thời gian xác định bởi
     * <i>sleepAfterFaile</i>.
     *
     * @throws Exception nếu có lỗi xảy ra
     */
    protected abstract void onLoop() throws Exception;
}
