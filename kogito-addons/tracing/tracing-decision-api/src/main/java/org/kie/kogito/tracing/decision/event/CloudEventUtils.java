/*
 *  Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.kie.kogito.tracing.decision.event;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.jackson.JsonFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CloudEventUtils {

    private static final Logger LOG = LoggerFactory.getLogger(CloudEventUtils.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper().registerModule(JsonFormat.getCloudEventJacksonModule());

    public static <E> Optional<CloudEvent> build(String id,
                                                 URI source,
                                                 E data,
                                                 Class<E> dataType) {
        try {
            byte[] bytes = OBJECT_MAPPER.writeValueAsBytes(data);
            return Optional.of(CloudEventBuilder.v1()
                    .withType(dataType.getName())
                    .withId(id)
                    .withSource(source)
                    .withData(bytes)
                    .build());
        } catch (JsonProcessingException e) {
            LOG.error("Unable to serialize CloudEvent data", e);
            return Optional.empty();
        }
    }

    public static String encode(CloudEvent event) {
        try {
            return OBJECT_MAPPER.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            LOG.error("Unable to encode CloudEvent", e);
            return null;
        }
    }

    public static CloudEvent decode(String json) {
        try {
            return OBJECT_MAPPER.readValue(json, CloudEvent.class);
        } catch (JsonProcessingException e) {
            LOG.error("Unable to decode CloudEvent", e);
            return null;
        }
    }

    public static URI uriFromString(String input) {
        return URI.create(urlEncode(input));
    }

    public static String urlEncode(String input) {
        try {
            return URLEncoder.encode(input, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    private CloudEventUtils() {
    }
}
