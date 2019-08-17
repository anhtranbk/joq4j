package org.joq4j.serde;

import java.io.InputStream;
import java.io.ObjectInputStream;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
@SuppressWarnings("unchecked")
public class JavaDeserialize implements Deserializer {

    @Override
    public Object deserialize(InputStream in) {
        try {
            ObjectInputStream ois = new ObjectInputStream(in);
            return ois.readObject();
        } catch (Exception e) {
            throw new SerdeException(e);
        }
    }
}
