package org.joq4j.serde;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class JavaSerdeFactory implements SerdeFactory {

    @Override
    public Serializer createSerializer() {
        return (input, out) -> {
            try {
                ObjectOutputStream oos = new ObjectOutputStream(out);
                oos.writeObject(input);
            } catch (Exception e) {
                throw new SerdeException(e);
            }
        };
    }

    @Override
    public Deserializer createDeserializer() {
        return in -> {
            try {
                ObjectInputStream ois = new ObjectInputStream(in);
                return ois.readObject();
            } catch (Exception e) {
                throw new SerdeException(e);
            }
        };
    }
}
