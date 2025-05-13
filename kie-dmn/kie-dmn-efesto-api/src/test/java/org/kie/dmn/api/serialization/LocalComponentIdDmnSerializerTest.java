/*
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
package org.kie.dmn.api.serialization;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import org.junit.jupiter.api.Test;
import org.kie.dmn.api.identifiers.LocalComponentIdDmn;
import org.kie.efesto.common.core.serialization.ModelLocalUriIdSerializer;

import static org.assertj.core.api.Assertions.assertThat;


class LocalComponentIdDmnSerializerTest {


//    @Test
//    void serialize() throws IOException {
//        String nameSpace = "https://kiegroup.org/dmn/_FBA17BF4-BC04-4C16-9305-40E8B4B2FECB";
//        String name = "NSEW";
//        LocalComponentIdDmn toSerialize = new LocalComponentIdDmn(nameSpace, name);
//        Writer jsonWriter = new StringWriter();
//        JsonGenerator jsonGenerator = new JsonFactory().createGenerator(jsonWriter);
//        SerializerProvider serializerProvider = new ObjectMapper().getSerializerProvider();
//        new LocalComponentIdDmnSerializer().serialize(toSerialize, jsonGenerator, serializerProvider);
//        jsonGenerator.flush();
//        String expected = "{\"model\":\"dmn\",\"basePath\":\"/https%3A/kiegroup.org/dmn/_FBA17BF4-BC04-4C16-9305-40E8B4B2FECB/NSEW\",\"fullPath\":\"/dmn/https%3A/kiegroup.org/dmn/_FBA17BF4-BC04-4C16-9305-40E8B4B2FECB/NSEW\"}";
//        assertThat(jsonWriter.toString()).isEqualTo(expected);
//    }

}