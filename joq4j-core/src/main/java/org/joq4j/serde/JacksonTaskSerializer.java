package org.joq4j.serde;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.io.SerializedString;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.joq4j.Task;

import java.io.IOException;

public class JacksonTaskSerializer extends StdSerializer<Task> {
    private final JavaSerialization javaSer;

    protected JacksonTaskSerializer(Class<Task> t, JavaSerialization javaSer) {
        super(t);
        this.javaSer = javaSer;
    }

    @Override
    public void serialize(Task task, JsonGenerator jsonGenerator,
                          SerializerProvider serializerProvider) throws IOException {
        String serialized = javaSer.serializeAsBase64(task, this.handledType());
        jsonGenerator.writeString(new SerializedString(serialized));
    }
}
