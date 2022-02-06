package org.joq4j.serde;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

@SuppressWarnings("unchecked")
public class DefaultJavaSerialization implements JavaSerialization {

    @Override
    public <T> void serialize(T input, Class<T> tClass, OutputStream out) {
        try {
            ObjectOutputStream os = new ObjectOutputStream(out);
            os.writeObject(input);
        } catch (Exception e) {
            throw new SerializationException(e);
        }
    }

    @Override
    public <T> T deserialize(InputStream in, Class<T> tClass) {
        try {
            ObjectInputStream is = new ObjectInputStream(in);
            return (T) is.readObject();
        } catch (Exception e) {
            throw new SerializationException(e);
        }
    }
}
