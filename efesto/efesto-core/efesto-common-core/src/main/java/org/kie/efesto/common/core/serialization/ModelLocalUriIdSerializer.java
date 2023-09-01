package org.kie.efesto.common.core.serialization;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.StringTokenizer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.kie.efesto.common.api.identifiers.ModelLocalUriId;

import static org.kie.efesto.common.api.identifiers.LocalUri.SLASH;

public class ModelLocalUriIdSerializer extends StdSerializer<ModelLocalUriId> {

    private static final long serialVersionUID = 5014755163979962781L;

    public ModelLocalUriIdSerializer() {
        this(null);
    }

    public ModelLocalUriIdSerializer(Class<ModelLocalUriId> t) {
        super(t);
    }

    @Override
    public void serialize(ModelLocalUriId value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeStringField("model", value.model());
        gen.writeStringField("basePath", decodedPath(value.basePath()));
        gen.writeStringField("fullPath", decodedPath(value.fullPath()));
        gen.writeEndObject();
    }


    static String decodedPath(String toDecode) {
        StringTokenizer tok = new StringTokenizer(toDecode, SLASH);
        StringBuilder builder = new StringBuilder();
        while (tok.hasMoreTokens()) {
            builder.append(SLASH);
            builder.append(decodeString(tok.nextToken()));
        }
        return builder.toString();
    }

    static String decodeString(String toDecode) {
        return URLDecoder.decode(toDecode, StandardCharsets.UTF_8);
    }

}
