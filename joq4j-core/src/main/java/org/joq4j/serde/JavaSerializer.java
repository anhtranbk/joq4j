package org.joq4j.serde;

import java.io.ObjectOutputStream;
import java.io.OutputStream;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public class JavaSerializer implements Serializer {

    @Override
    public void serialize(Object input, OutputStream out) {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(out);
            oos.writeObject(input);
        } catch (Exception e) {
            throw new SerdeException(e);
        }
    }
}
