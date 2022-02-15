package org.joq4j;

import lombok.Data;
import lombok.experimental.Accessors;
import org.joq4j.config.ConfigDescriptor;
import org.joq4j.config.Configurable;

@Accessors(chain = true, fluent = true)
@Data
@Configurable
public class QueueOptions {

    private int maxSize;

    private int maxConcurrent;

    private long defaultTimeout = 1000;

    @ConfigDescriptor(
            name = "queue.messageEncoderClass",
            defaultValue = "org.joq4j.serde.JacksonMessageEncoder"
    )
    private String messageEncoderClass;

    @ConfigDescriptor(name = "queue.storeResult")
    private boolean storeResult = true;

    @ConfigDescriptor(name = "queue.sentTaskEvents")
    private boolean sentEvents = false;
}
