package org.joq4j.serde;

public interface SerdeFactory {

    Serializer createSerializer();

    Deserializer createDeserializer();
}
