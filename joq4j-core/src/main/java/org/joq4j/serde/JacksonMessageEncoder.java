package org.joq4j.serde;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.joq4j.Task;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class JacksonMessageEncoder implements MessageEncoder {
    private static final ObjectMapper om;
    static {
        om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

        SimpleModule module = new SimpleModule();
        JavaSerialization javaSer = new DefaultJavaSerialization();
        module.addSerializer(Task.class, new JacksonTaskSerializer(Task.class, javaSer));
        module.addDeserializer(Task.class, new JacksonTaskDeserializer(Task.class, javaSer));
        om.registerModule(module);
    }

    @Override
    public <T> void write(T input, OutputStream out) {
        try {
            om.writeValue(out, input);
        } catch (IOException e) {
            throw new EncodingException(e);
        }
    }

    @Override
    public <T> T read(InputStream in, Class<T> tClass) {
        try {
            return om.readValue(in, tClass);
        } catch (IOException e) {
            throw new EncodingException(e);
        }
    }
}
