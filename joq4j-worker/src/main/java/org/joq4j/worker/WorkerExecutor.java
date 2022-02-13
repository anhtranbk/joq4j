package org.joq4j.worker;

import org.joq4j.Task;

public interface WorkerExecutor {

    void processTask(Task task);
}
