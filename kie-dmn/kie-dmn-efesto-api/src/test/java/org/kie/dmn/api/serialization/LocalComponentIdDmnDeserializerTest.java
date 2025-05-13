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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.kie.dmn.api.identifiers.LocalComponentIdDmn;

import static org.assertj.core.api.Assertions.assertThat;

class LocalComponentIdDmnDeserializerTest {


//    @Test
//    void deserialize() throws IOException {
//        String json = "{\"model\":\"dmn\",\"basePath\":\"/https%3A/kiegroup.org/dmn/_FBA17BF4-BC04-4C16-9305-40E8B4B2FECB/NSEW\",\"fullPath\":\"/dmn/https%3A/kiegroup.org/dmn/_FBA17BF4-BC04-4C16-9305-40E8B4B2FECB/NSEW\"}";
//        ObjectMapper mapper = new ObjectMapper();
//        InputStream stream = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
//        JsonParser parser = mapper.getFactory().createParser(stream);
//        DeserializationContext ctxt = mapper.getDeserializationContext();
//        LocalComponentIdDmn retrieved = new LocalComponentIdDmnDeserializer().deserialize(parser, ctxt);
//        String nameSpace = "https://kiegroup.org/dmn/_FBA17BF4-BC04-4C16-9305-40E8B4B2FECB";
//        String name = "NSEW";
//        LocalComponentIdDmn expected = new LocalComponentIdDmn(nameSpace, name);
//        assertThat(retrieved).isEqualTo(expected);
//    }

}