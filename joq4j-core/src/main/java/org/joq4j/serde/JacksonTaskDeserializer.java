package org.joq4j.serde;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.TextNode;
import org.joq4j.Task;

import java.io.IOException;

public class JacksonTaskDeserializer extends StdDeserializer<Task> {
    private final JavaSerialization javaSer;

    protected JacksonTaskDeserializer(Class<Task> vc, JavaSerialization javaSer) {
        super(vc);
        this.javaSer = javaSer;
    }

    @Override
    public Task deserialize(JsonParser jsonParser,
                            DeserializationContext deserializationContext) throws IOException {
        TreeNode node = jsonParser.readValueAsTree();
        String serialized = ((TextNode) node).asText();
        return (Task) javaSer.deserializeFromBase64(serialized, this.handledType());
    }
}
