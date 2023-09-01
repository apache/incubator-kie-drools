package org.kie.efesto.common.core.serialization;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.junit.jupiter.api.Test;
import org.kie.efesto.common.api.identifiers.LocalUri;
import org.kie.efesto.common.api.identifiers.ModelLocalUriId;

import static org.assertj.core.api.Assertions.assertThat;

class ModelLocalUriIdSerializerTest {

    @Test
    void serializeDecodedPath() throws IOException {
        String path = "/example/some-id/instances/some-instance-id";
        LocalUri parsed = LocalUri.parse(path);
        ModelLocalUriId modelLocalUriId = new ModelLocalUriId(parsed);
        Writer jsonWriter = new StringWriter();
        JsonGenerator jsonGenerator = new JsonFactory().createGenerator(jsonWriter);
        SerializerProvider serializerProvider = new ObjectMapper().getSerializerProvider();
        new ModelLocalUriIdSerializer().serialize(modelLocalUriId, jsonGenerator, serializerProvider);
        jsonGenerator.flush();
        String expected = "{\"model\":\"example\",\"basePath\":\"/some-id/instances/some-instance-id\"," +
                "\"fullPath\":\"/example/some-id/instances/some-instance-id\"}";
        assertThat(jsonWriter.toString()).isEqualTo(expected);
    }

    @Test
    void serializeEncodedPath() throws IOException {
        String path = "/To+decode+first+part/To+decode+second+part/To+decode+third+part/";
        LocalUri parsed = LocalUri.parse(path);
        ModelLocalUriId modelLocalUriId = new ModelLocalUriId(parsed);
        Writer jsonWriter = new StringWriter();
        JsonGenerator jsonGenerator = new JsonFactory().createGenerator(jsonWriter);
        SerializerProvider serializerProvider = new ObjectMapper().getSerializerProvider();
        new ModelLocalUriIdSerializer().serialize(modelLocalUriId, jsonGenerator, serializerProvider);
        jsonGenerator.flush();
        String expected = "{\"model\":\"To%2Bdecode%2Bfirst%2Bpart\"," +
                "\"basePath\":\"/To+decode+second+part/To+decode+third+part\"," +
                "\"fullPath\":\"/To+decode+first+part/To+decode+second+part/To+decode+third+part\"}";
        assertThat(jsonWriter.toString()).isEqualTo(expected);
    }

    @Test
    void decodedPath() {
        String toDecode = "To+decode+first+part/To+decode+second+part/To+decode+third+part/";
        String retrieved = ModelLocalUriIdSerializer.decodedPath(toDecode);
        String expected = "/To decode first part/To decode second part/To decode third part";
        assertThat(retrieved).isEqualTo(expected);
    }

    @Test
    void decodeString() {
        String toDecode = "To+decode+string";
        String retrieved = ModelLocalUriIdSerializer.decodeString(toDecode);
        String expected = "To decode string";
        assertThat(retrieved).isEqualTo(expected);
    }
}