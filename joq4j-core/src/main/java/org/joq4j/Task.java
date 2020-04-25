package org.joq4j;

import java.io.Serializable;
import java.util.concurrent.Callable;

public interface Task extends Serializable, Callable<Object> {

    static Task doNothing() {
        return new DoNothingTask();
    }

    default boolean isCancelable() {
        return false;
    }

    class DoNothingTask implements Task {
        @Override
        public boolean isCancelable() {
            return false;
        }

        @Override
        public Object call() throws Exception {
            return null;
        }
    }
}
