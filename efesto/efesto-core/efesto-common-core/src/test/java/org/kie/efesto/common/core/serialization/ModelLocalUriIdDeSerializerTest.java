package org.kie.efesto.common.core.serialization;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.kie.efesto.common.api.identifiers.LocalUri;
import org.kie.efesto.common.api.identifiers.ModelLocalUriId;

import static org.assertj.core.api.Assertions.assertThat;

class ModelLocalUriIdDeSerializerTest {

    @Test
    void deserializeDecodedPath() throws IOException {
        String json = "{\"model\":\"example\",\"basePath\":\"/some-id/instances/some-instance-id\"," +
                "\"fullPath\":\"/example/some-id/instances/some-instance-id\"}";
        ObjectMapper mapper = new ObjectMapper();
        InputStream stream = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
        JsonParser parser = mapper.getFactory().createParser(stream);
        DeserializationContext ctxt = mapper.getDeserializationContext();
        ModelLocalUriId retrieved = new ModelLocalUriIdDeSerializer().deserialize(parser, ctxt);

        String path = "/example/some-id/instances/some-instance-id";
        LocalUri parsed = LocalUri.parse(path);
        ModelLocalUriId expected = new ModelLocalUriId(parsed);
        assertThat(retrieved).isEqualTo(expected);
    }

    @Test
    void deserializeEncodedPath() throws IOException {
        String json = "{\"model\":\"To%2Bdecode%2Bfirst%2Bpart\"," +
                "\"basePath\":\"/To+decode+second+part/To+decode+third+part\"," +
                "\"fullPath\":\"/To+decode+first+part/To+decode+second+part/To+decode+third+part\"}";
        ObjectMapper mapper = new ObjectMapper();
        InputStream stream = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
        JsonParser parser = mapper.getFactory().createParser(stream);
        DeserializationContext ctxt = mapper.getDeserializationContext();
        ModelLocalUriId retrieved = new ModelLocalUriIdDeSerializer().deserialize(parser, ctxt);
        String path = "/To+decode+first+part/To+decode+second+part/To+decode+third+part/";
        LocalUri parsed = LocalUri.parse(path);
        ModelLocalUriId expected = new ModelLocalUriId(parsed);
        assertThat(retrieved).isEqualTo(expected);
    }
}