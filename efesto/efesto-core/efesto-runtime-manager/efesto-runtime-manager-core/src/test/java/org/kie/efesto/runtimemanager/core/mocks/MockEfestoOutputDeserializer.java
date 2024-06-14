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
package org.kie.efesto.runtimemanager.core.mocks;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.kie.efesto.common.api.exceptions.KieEfestoCommonException;
import org.kie.efesto.common.api.identifiers.ModelLocalUriId;
import org.kie.efesto.common.core.utils.JSONUtils;

import static org.kie.efesto.common.core.utils.JSONUtils.getObjectMapper;

public class MockEfestoOutputDeserializer extends StdDeserializer<MockEfestoOutput> {

    private static final long serialVersionUID = 5014755163979962781L;

    public MockEfestoOutputDeserializer() {
        this(null);
    }

    public MockEfestoOutputDeserializer(Class<MockEfestoOutput> t) {
        super(t);
    }

    @Override
    public MockEfestoOutput deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);
        try {
            String modelLocalUriIdString = node.get("modelLocalUriId").toString();
            ModelLocalUriId modelLocalUriId = JSONUtils.getModelLocalUriIdObject(modelLocalUriIdString);
            String outputData = getObjectMapper().readValue(node.get("outputData").toString(), String.class);
            return new MockEfestoOutput(modelLocalUriId, outputData);
        } catch (Exception e) {
            throw new KieEfestoCommonException(String.format("Failed to deserialize %s as MockEfestoOutput", node), e);
        }
    }


}
