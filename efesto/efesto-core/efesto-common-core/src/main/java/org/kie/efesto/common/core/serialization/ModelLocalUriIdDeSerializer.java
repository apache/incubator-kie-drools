package org.kie.efesto.common.core.serialization;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.kie.efesto.common.api.identifiers.LocalUri;
import org.kie.efesto.common.api.identifiers.ModelLocalUriId;

public class ModelLocalUriIdDeSerializer extends StdDeserializer<ModelLocalUriId> {

    private static final long serialVersionUID = -3468047979532504909L;

    public ModelLocalUriIdDeSerializer() {
        this(null);
    }

    public ModelLocalUriIdDeSerializer(Class<ModelLocalUriId> t) {
        super(t);
    }

    @Override
    public ModelLocalUriId deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);
        String path = node.get("fullPath").asText();
        return new ModelLocalUriId(LocalUri.parse(path));
    }
}
