package org.joq4j;

import lombok.Data;
import lombok.experimental.Accessors;
import org.joq4j.encoding.Encoder;
import org.joq4j.encoding.TaskSerializer;

@Accessors(chain = true)
public @Data class QueueOptions {

    private int maxSize;

    private long defaultTimeout;

    private boolean async;

    private Encoder encoder;

    private TaskSerializer taskSerializer;
}
