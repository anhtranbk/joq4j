package org.joq4j;

import lombok.Data;
import lombok.experimental.Accessors;
import org.joq4j.config.ConfigDescriptor;
import org.joq4j.config.Configurable;
import org.joq4j.encoding.JacksonMessageEncoder;
import org.joq4j.encoding.JavaTaskSerializer;
import org.joq4j.encoding.MessageEncoder;
import org.joq4j.encoding.TaskSerializer;

@Accessors(chain = true, fluent = true)
public @Data class QueueOptions implements Configurable {

    private int maxSize;

    private int maxConcurrent;

    private long defaultTimeout = 1000;

    private MessageEncoder messageEncoder = new JacksonMessageEncoder();

    private TaskSerializer taskSerializer = new JavaTaskSerializer();

    @ConfigDescriptor(name = "joq4j.worker.storeResult")
    private boolean storeResult = true;

    @ConfigDescriptor(name = "joq4j.worker.storeErrorEventIfIgnored")
    private boolean storeErrorEventIfIgnored = true;

    @ConfigDescriptor(name = "joq4j.worker.sentEvents")
    private boolean sentEvents = false;
}
