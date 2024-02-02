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
package org.kie.kogito.addon.cloudevents.quarkus.deployment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.reactive.messaging.OnOverflow.Strategy;
import org.kie.kogito.event.KogitoEventStreams;

public class ChannelMappingStrategy {

    private ChannelMappingStrategy() {
    }

    private static final String OUTGOING_PREFIX = "mp.messaging.outgoing.";
    private static final String INCOMING_PREFIX = "mp.messaging.incoming.";

    private static final String KOGITO_MESSAGING_PREFIX = "kogito.addon.messaging.";
    private static final String KOGITO_INCOMING_PREFIX = "kogito.addon.messaging.incoming.";
    private static final String KOGITO_OUTGOING_PREFIX = "kogito.addon.messaging.outgoing.";
    private static final String INCOMING_TRIGGER = KOGITO_INCOMING_PREFIX + "trigger.";
    private static final String OUTGOING_TRIGGER = KOGITO_OUTGOING_PREFIX + "trigger.";
    private static final String INCOMING_DEFAULT_CHANNEL = KOGITO_INCOMING_PREFIX + "defaultName";
    private static final String OUTGOING_DEFAULT_CHANNEL = KOGITO_OUTGOING_PREFIX + "defaultName";

    private static final String CLOUD_EVENT_MODE = KOGITO_OUTGOING_PREFIX + "cloudEventMode";

    private static final String MARSHALLER_PREFIX = KOGITO_MESSAGING_PREFIX + "marshaller.";
    private static final String UNMARSHALLLER_PREFIX = KOGITO_MESSAGING_PREFIX + "unmarshaller.";
    private static final String KOGITO_EMITTER_PREFIX = KOGITO_MESSAGING_PREFIX + "emitter.";
    private static final String OVERFLOW_STRATEGY_PROP = "overflow-strategy";
    private static final String BUFFER_SIZE_PROP = "buffer-size";

    public static Collection<ChannelInfo> getChannelMapping() {
        Config config = ConfigProvider.getConfig();
        Map<String, Collection<String>> inTriggers = new HashMap<>();
        Map<String, Collection<String>> outTriggers = new HashMap<>();

        for (String property : config.getPropertyNames()) {
            if (property.startsWith(INCOMING_TRIGGER)) {
                addTrigger(config, INCOMING_TRIGGER, property, inTriggers);
            } else if (property.startsWith(OUTGOING_TRIGGER)) {
                addTrigger(config, OUTGOING_TRIGGER, property, outTriggers);
            }
        }

        Collection<ChannelInfo> result = new ArrayList<>();
        final String defaultIncomingChannel = config.getOptionalValue(INCOMING_DEFAULT_CHANNEL, String.class).orElse(KogitoEventStreams.INCOMING);
        final String defaultOutgoingChannel = config.getOptionalValue(OUTGOING_DEFAULT_CHANNEL, String.class).orElse(KogitoEventStreams.OUTGOING);
        for (String property : config.getPropertyNames()) {
            if (property.startsWith(INCOMING_PREFIX) && property.endsWith(".connector")) {
                result.add(getChannelInfo(config, property, INCOMING_PREFIX, true, defaultIncomingChannel, inTriggers));
            } else if (property.startsWith(OUTGOING_PREFIX) && property.endsWith(".connector")) {
                result.add(getChannelInfo(config, property, OUTGOING_PREFIX, false, defaultOutgoingChannel, outTriggers));
            }
        }
        return result;
    }

    private static void addTrigger(Config config, String prefix, String property, Map<String, Collection<String>> triggers) {
        String channelName = config.getValue(property, String.class);
        String triggerName = property.substring(prefix.length());
        triggers.computeIfAbsent(channelName, ChannelMappingStrategy::initTriggers).add(triggerName);
    }

    private static Collection<String> initTriggers(String channelName) {
        Collection<String> result = new HashSet<>();
        result.add(channelName);
        return result;
    }

    private static ChannelInfo getChannelInfo(Config config, String property, String prefix, boolean isInput, String defaultChannelName, Map<String, Collection<String>> triggers) {
        String name = property.substring(prefix.length(), property.lastIndexOf('.'));
        return new ChannelInfo(name, triggers.getOrDefault(name, Collections.singleton(name)),
                getClassName(config.getOptionalValue(getPropertyName(prefix, name, "value." + (isInput ? "deserializer" : "serializer")), String.class)), isInput,
                name.equals(defaultChannelName), config.getOptionalValue((isInput ? UNMARSHALLLER_PREFIX : MARSHALLER_PREFIX) + name, String.class),
                isInput ? Optional.empty() : onOverflowInfo(config, name), cloudEventMode(config, name, property));
    }

    private static Optional<CloudEventMode> cloudEventMode(Config config, String name, String property) {
        if (!config.getOptionalValue("kogito.messaging.as-cloudevents", Boolean.class).orElse(true)) {
            return Optional.empty();
        }
        Optional<CloudEventMode> cloudEventMode = getCloudEventMode(config, CLOUD_EVENT_MODE + "." + name);
        if (cloudEventMode.isPresent()) {
            return cloudEventMode;
        }
        cloudEventMode = getCloudEventMode(config, CLOUD_EVENT_MODE);
        if (cloudEventMode.isPresent()) {
            return cloudEventMode;
        }
        // if no config, infer default from connector type
        String connector = config.getValue(property, String.class);
        return Optional.of(connector.equals("quarkus-http") ? CloudEventMode.BINARY : CloudEventMode.STRUCTURED);
    }

    private static Optional<CloudEventMode> getCloudEventMode(Config config, String propName) {
        return config.getOptionalValue(propName, String.class).map(String::toUpperCase).map(CloudEventMode::valueOf);
    }

    private static Optional<OnOverflowInfo> onOverflowInfo(Config config, String name) {
        final String namePrefix = KOGITO_EMITTER_PREFIX + name + ".";
        Optional<Strategy> strategy = config.getOptionalValue(namePrefix + OVERFLOW_STRATEGY_PROP, String.class).map(Strategy::valueOf);
        Optional<Long> bufferSize = config.getOptionalValue(namePrefix + BUFFER_SIZE_PROP, Long.class);
        return strategy.map(s -> new OnOverflowInfo(s, bufferSize)).or(() -> bufferSize.isPresent() ? Optional.of(new OnOverflowInfo(Strategy.BUFFER, bufferSize)) : Optional.empty()).or(
                () -> config.getOptionalValue(KOGITO_EMITTER_PREFIX + OVERFLOW_STRATEGY_PROP, String.class).map(Strategy::valueOf).map(s -> new OnOverflowInfo(s, Optional.empty())));
    }

    private static String getClassName(Optional<String> serializerClassName) {
        if (serializerClassName.isPresent()) {
            String value = serializerClassName.get();
            int indexOf = value.lastIndexOf(".");
            if (indexOf >= 0) {
                value = value.substring(indexOf + 1);
            }
            // Checking for StringSerializer or ByteArraySerializer in order to keep backward compatibility 
            if (value.startsWith("String")) {
                return "String";
            } else if (value.startsWith("ByteArray")) {
                return "byte[]";
            }
        }
        // Default behavior is to use the object marshaller. 
        return "Object";
    }

    private static final String getPropertyName(String prefix, String name, String suffix) {
        return prefix + name + "." + suffix;
    }
}
