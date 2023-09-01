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