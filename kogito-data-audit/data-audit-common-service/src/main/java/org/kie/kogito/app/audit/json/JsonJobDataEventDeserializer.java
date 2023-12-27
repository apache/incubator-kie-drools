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
package org.kie.kogito.app.audit.json;

import java.io.IOException;
import java.time.OffsetDateTime;

import org.kie.kogito.event.job.JobInstanceDataEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class JsonJobDataEventDeserializer extends StdDeserializer<JobInstanceDataEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(JsonJobDataEventDeserializer.class);

    private static final long serialVersionUID = 6152014726577574241L;

    public JsonJobDataEventDeserializer() {
        this(null);
    }

    public JsonJobDataEventDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public JobInstanceDataEvent deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        JsonNode node = jp.getCodec().readTree(jp);
        LOGGER.debug("Deserialize process instance data event: {}", node);

        JobInstanceDataEvent event = new JobInstanceDataEvent(
                node.has("type") ? node.get("type").asText() : null,
                node.has("source") ? node.get("source").asText() : null,
                node.has("data") ? node.get("data").binaryValue() : null,
                node.has("kogitoprocinstanceid") ? node.get("kogitoprocinstanceid").asText() : null,
                node.has("kogitorootprociid") ? node.get("kogitorootprociid").asText() : null,
                node.has("kogitoprocid") ? node.get("kogitoprocid").asText() : null,
                node.has("kogitorootprocid") ? node.get("kogitorootprocid").asText() : null,
                node.has("kogitoidentity") ? node.get("kogitoidentity").asText() : null);

        event.setId(node.has("id") ? node.get("id").asText() : null);
        event.setKogitoIdentity(node.has("kogitoidentity") ? node.get("kogitoidentity").asText() : null);
        event.setTime(node.has("time") ? jp.getCodec().treeToValue(node.get("time"), OffsetDateTime.class) : null);

        return event;
    }
}