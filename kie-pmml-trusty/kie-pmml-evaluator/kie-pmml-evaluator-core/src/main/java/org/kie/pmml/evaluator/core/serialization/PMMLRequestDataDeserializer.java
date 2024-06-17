/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.pmml.evaluator.core.serialization;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.kie.pmml.api.dto.PMMLRequestData;
import org.kie.pmml.api.exceptions.KiePMMLInternalException;

import static org.kie.efesto.common.core.utils.JSONUtils.getObjectMapper;

public class PMMLRequestDataDeserializer extends StdDeserializer<PMMLRequestData> {

    private static final ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.configure(JsonParser.Feature.IGNORE_UNDEFINED, true);
        mapper.configure(JsonGenerator.Feature.IGNORE_UNKNOWN, true);
    }

    public PMMLRequestDataDeserializer() {
        this(null);
    }

    public PMMLRequestDataDeserializer(Class<PMMLRequestData> t) {
        super(t);
    }

    @Override
    public PMMLRequestData deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);
        if (node.get("correlationId") == null || node.get("modelName") == null) {
            throw new KiePMMLInternalException(String.format("The given node %s does not contains the required " +
                                                                     "`correlationId` or `modelName` tags", node));
        }
        String correlationId = node.get("correlationId").asText();
        String modelName = node.get("modelName").asText();
        PMMLRequestData toReturn = new PMMLRequestData(correlationId, modelName);
        if (node.get("source") != null) {
            toReturn.setSource(node.get("source").asText());
        }
        if (node.get("requestParams") != null) {
            ArrayNode requestParamNode = (ArrayNode) node.get("requestParams");
            requestParamNode.elements().forEachRemaining(jsonNode -> {
                try {
                    String name = jsonNode.get("name").asText();
                    Object value = getObjectMapper().readValue(jsonNode.get("value").toString(), Object.class);
                    toReturn.addRequestParam(name, value);
                } catch (JsonProcessingException e) {
                    throw new KiePMMLInternalException(String.format("Failed to deserialize the node %s", jsonNode), e);
                }
            });
        }
        return toReturn;
    }
}
