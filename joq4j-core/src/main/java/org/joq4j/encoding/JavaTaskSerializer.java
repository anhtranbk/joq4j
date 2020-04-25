package org.joq4j.encoding;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

@SuppressWarnings("unchecked")
public class JavaTaskSerializer implements TaskSerializer {

    @Override
    public <T> void write(T input, Class<T> tClass, OutputStream out) {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(out);
            oos.writeObject(input);
        } catch (Exception e) {
            throw new EncodingException(e);
        }
    }

    @Override
    public <T> T read(InputStream in, Class<T> tClass) {
        try {
            ObjectInputStream ois = new ObjectInputStream(in);
            return (T) ois.readObject();
        } catch (Exception e) {
            throw new EncodingException(e);
        }
    }
}
