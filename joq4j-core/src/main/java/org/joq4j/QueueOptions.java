package org.joq4j;

import lombok.Data;
import lombok.experimental.Accessors;
import org.joq4j.config.ConfigDescriptor;
import org.joq4j.config.Configurable;
import org.joq4j.encoding.MessageEncoder;
import org.joq4j.encoding.TaskSerializer;

@Accessors(chain = true, fluent = true)
public @Data class QueueOptions implements Configurable {

    private int maxSize;

    private long defaultTimeout;

    private MessageEncoder messageEncoder;

    private TaskSerializer taskSerializer;

    @ConfigDescriptor(name = "joq4j.worker.storeResult")
    private boolean storeResult = true;

    @ConfigDescriptor(name = "joq4j.worker.storeErrorEventIfIgnored")
    private boolean storeErrorEventIfIgnored = true;

    @ConfigDescriptor(name = "joq4j.worker.sentEvents")
    private boolean sentEvents = false;
}
