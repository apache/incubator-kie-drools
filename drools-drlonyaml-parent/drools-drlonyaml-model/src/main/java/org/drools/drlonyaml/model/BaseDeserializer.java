package org.drools.drlonyaml.model;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class BaseDeserializer extends StdDeserializer<Base> { 

    public BaseDeserializer() { 
        this(null); 
    } 

    public BaseDeserializer(Class<?> vc) { 
        super(vc); 
    }

    @Override
    public Base deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonNode node = jp.getCodec().readTree(jp);
        if (node.get("given") != null) { // TODO maybe enfore XOR checks.
            return jp.getCodec().treeToValue(node, Pattern.class);
        } else if (node.get("exists") != null) {
            return jp.getCodec().treeToValue(node, Exists.class);
        } else {
            throw new UnsupportedOperationException();
        }
    }
}
