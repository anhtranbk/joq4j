package org.joq4j;

import lombok.Data;
import lombok.experimental.Accessors;
import org.joq4j.config.ConfigDescriptor;
import org.joq4j.config.Configurable;
import org.joq4j.serde.JacksonMessageEncoder;
import org.joq4j.serde.MessageEncoder;

@Accessors(chain = true, fluent = true)
@Data
@Configurable
public class QueueOptions {

    private int maxSize;

    private int maxConcurrent;

    private long defaultTimeout = 1000;

    private MessageEncoder messageEncoder = new JacksonMessageEncoder();

    @ConfigDescriptor(name = "joq4j.worker.storeResult")
    private boolean storeResult = true;

    @ConfigDescriptor(name = "joq4j.worker.storeErrorEventIfIgnored")
    private boolean storeErrorEventIfIgnored = true;

    @ConfigDescriptor(name = "joq4j.worker.sentEvents")
    private boolean sentEvents = false;
}
