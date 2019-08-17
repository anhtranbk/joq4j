package org.joq4j;

import lombok.Data;
import lombok.experimental.Accessors;
import org.joq4j.serde.Deserializer;
import org.joq4j.serde.Serializer;

@Accessors(chain = true)
public @Data class QueueOptions {

    private int maxSize;

    private long defaultTimeout;

    private boolean async;

    private Serializer serializer;

    private Deserializer deserializer;
}
