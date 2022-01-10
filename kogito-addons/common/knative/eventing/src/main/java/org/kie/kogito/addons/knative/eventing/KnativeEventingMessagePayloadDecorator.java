/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.kogito.addons.knative.eventing;

import java.io.IOException;

import org.kie.kogito.addon.cloudevents.message.MessagePayloadDecorator;
import org.kie.kogito.event.cloudevents.utils.CloudEventUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * {@link MessagePayloadDecorator} to process Knative Eventing CloudEvents Override.
 * 
 * @see <a href="https://knative.dev/docs/developer/eventing/sources/sinkbinding/reference/#cloudevent-overrides">Knative Eventing SinkBinding - CloudEvent Overrides</a>
 */
public class KnativeEventingMessagePayloadDecorator implements MessagePayloadDecorator {

    public static final String K_CE_OVERRIDES = "K_CE_OVERRIDES";
    private static final Logger LOGGER = LoggerFactory.getLogger(KnativeEventingMessagePayloadDecorator.class);

    private final ObjectMapper mapper;

    public KnativeEventingMessagePayloadDecorator() {
        mapper = CloudEventUtils.Mapper.mapper();
    }

    private JsonNode serializeEnvCeOverrides() {
        final String ceOverridesValue = readEnvCeOverrides();
        if (ceOverridesValue == null || "".equals(ceOverridesValue)) {
            return null;
        }
        try {
            final CeOverrides ceOverrides = this.mapper.readValue(ceOverridesValue, CeOverrides.class);
            return this.mapper.valueToTree(ceOverrides.getExtensions());
        } catch (JsonProcessingException e) {
            LOGGER.warn("The variable {} doesn't have a valid JSON value: {}. Skipping override.", K_CE_OVERRIDES, e.getMessage());
            return null;
        }
    }

    // visible for testing, don't make it public
    String readEnvCeOverrides() {
        return System.getenv(K_CE_OVERRIDES);
    }

    @Override
    public String decorate(final String jsonPayload) {
        final JsonNode ceOverrides = this.serializeEnvCeOverrides();
        if (ceOverrides == null) {
            LOGGER.debug("{} variable not present in the environment or it's empty", K_CE_OVERRIDES);
            return jsonPayload;
        }
        if (!CloudEventUtils.isCloudEvent(jsonPayload)) {
            LOGGER.warn("{} payload is not a valid CloudEvent, skipping {}.", jsonPayload, K_CE_OVERRIDES);
            return jsonPayload;
        }
        try {
            final ObjectNode payloadNode = (ObjectNode) this.mapper.readTree(jsonPayload);
            return mapper.writeValueAsString(payloadNode.setAll((ObjectNode) ceOverrides));
        } catch (IOException e) {
            LOGGER.error("Failed to override CloudEvents extensions with K_CE_OVERRIDES value", e);
            return jsonPayload;
        }
    }
}
