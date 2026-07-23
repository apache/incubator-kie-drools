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
package org.kie.kogito.jobs.service.json;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;

import org.kie.kogito.jobs.DurationExpirationTime;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class DurationExpirationTimeDeserializer extends StdDeserializer<DurationExpirationTime> {

    private static final long serialVersionUID = -8307549297456060422L;

    public DurationExpirationTimeDeserializer() {
        super(DurationExpirationTime.class);
    }

    @Override
    public DurationExpirationTime deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JacksonException {

        JsonNode node = jp.getCodec().readTree(jp);

        ZonedDateTime time = ctxt.readTreeAsValue(node.get("expirationTime"), ZonedDateTime.class);
        Long repeatInterval = null;
        if (node.has("repeatInterval")) {
            repeatInterval = node.get("repeatInterval").asLong();
        }
        Integer repeatLimit = null;
        if (node.has("repeatLimit")) {
            repeatLimit = node.get("repeatLimit").asInt(0);
        }

        Duration res = Duration.between(Instant.now(), time.toInstant());
        return DurationExpirationTime.repeat(res.toMillis(), repeatInterval, repeatLimit);
    }

}
