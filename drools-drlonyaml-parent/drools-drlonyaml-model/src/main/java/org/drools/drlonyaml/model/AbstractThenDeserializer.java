package org.drools.drlonyaml.model;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class AbstractThenDeserializer extends StdDeserializer<AbstractThen> { 

    public AbstractThenDeserializer() { 
        this(null); 
    } 

    public AbstractThenDeserializer(Class<?> vc) { 
        super(vc); 
    }

    @Override
    public AbstractThen deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonNode node = jp.getCodec().readTree(jp);
        if (node.isTextual()) {
            return StringThen.from(node.textValue());
/* if additional RHS/then will be needed for abstracting from Java (snippet), then the extension mechanism can support additional
   types providing something ~like:
        } else if (node.get("something") != null) {
          return jp.getCodec().treeToValue(node, ConcreteClass.class);*/
        } else {
            throw new UnsupportedOperationException();
        }
    }
}
