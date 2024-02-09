/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
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