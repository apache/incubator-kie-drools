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
package org.kie.kogito.codegen.process.events;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.event.KogitoEventStreams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChannelMappingStrategy {

    private static Logger LOG = LoggerFactory.getLogger(ChannelMappingStrategy.class);

    private ChannelMappingStrategy() {
    }

    private static final String OUTGOING_PREFIX = "mp.messaging.outgoing.";
    private static final String INCOMING_PREFIX = "mp.messaging.incoming.";

    private static final String KOGITO_MESSAGING_PREFIX = "kogito.addon.messaging.";
    private static final String KOGITO_MESSAGING_INCOMING_PREFIX = "kogito.addon.messaging.incoming.";
    private static final String KOGITO_MESSAGING_OUTGOING_PREFIX = "kogito.addon.messaging.outgoing.";
    private static final String KOGITO_MESSAGING_INCOMING_DEFAULT_CHANNEL = KOGITO_MESSAGING_INCOMING_PREFIX + "defaultName";
    private static final String KOGITO_MESSAGING_OUTGOING_DEFAULT_CHANNEL = KOGITO_MESSAGING_OUTGOING_PREFIX + "defaultName";
    private static final String KOGITO_MESSAGING_INCOMING_TRIGGER = KOGITO_MESSAGING_INCOMING_PREFIX + "trigger.";
    private static final String KOGITO_MESSAGING_OUTGOING_TRIGGER = KOGITO_MESSAGING_OUTGOING_PREFIX + "trigger.";

    private static final String KAFKA_PREFIX = "kogito.addon.cloudevents.kafka.";

    private static final String KAFKA_INCOMING_PREFIX = KAFKA_PREFIX + KogitoEventStreams.INCOMING + ".";
    private static final String KAFKA_OUTGOING_PREFIX = KAFKA_PREFIX + KogitoEventStreams.OUTGOING + ".";
    private static final String KAFKA_INCOMING_DEFAULT_CHANNEL = KAFKA_PREFIX + KogitoEventStreams.INCOMING;
    private static final String KAFKA_OUTGOING_DEFAULT_CHANNEL = KAFKA_PREFIX + KogitoEventStreams.OUTGOING;

    private static final String CLOUD_EVENT_MODE = "kogito.addon.messaging.outgoing.cloudEventMode";

    private static final String MARSHALLER_PREFIX = KOGITO_MESSAGING_PREFIX + "marshaller.";
    private static final String UNMARSHALLLER_PREFIX = KOGITO_MESSAGING_PREFIX + "unmarshaller.";

    public static Collection<ChannelInfo> getChannelMapping(KogitoBuildContext context) {
        List<ChannelInfo> channelsInfo = new ArrayList<>();
        channelsInfo.addAll(processQuarkus(context));
        channelsInfo.addAll(processSpring(context));
        return channelsInfo;
    }

    private static Collection<ChannelInfo> processSpring(KogitoBuildContext context) {
        Map<String, Collection<String>> inTriggers = new HashMap<>();
        Map<String, Collection<String>> outTriggers = new HashMap<>();

        for (String property : context.getApplicationProperties()) {
            if (property.startsWith(KOGITO_MESSAGING_INCOMING_TRIGGER)) {
                addTrigger(context, KOGITO_MESSAGING_INCOMING_TRIGGER, property, inTriggers);
            } else if (property.startsWith(KOGITO_MESSAGING_OUTGOING_TRIGGER)) {
                addTrigger(context, KOGITO_MESSAGING_OUTGOING_TRIGGER, property, outTriggers);
            }
        }

        Optional<String> inputDefaultChannel = context.getApplicationProperty(KAFKA_INCOMING_DEFAULT_CHANNEL, String.class);
        Optional<String> outputDefaultChannel = context.getApplicationProperty(KAFKA_OUTGOING_DEFAULT_CHANNEL, String.class);

        String defaultIncomingChannel = inputDefaultChannel.orElse(KogitoEventStreams.INCOMING);
        String defaultOutgoingChannel = outputDefaultChannel.orElse(KogitoEventStreams.OUTGOING);

        Collection<ChannelInfo> result = new ArrayList<>();
        for (String property : context.getApplicationProperties()) {
            buildChannelInfo(context, true, property, KAFKA_INCOMING_PREFIX, defaultIncomingChannel, inTriggers, Optional.of("String"), Optional.empty(), p -> !p.contains(".value."))
                    .ifPresent(result::add);
            buildChannelInfo(context, false, property, KAFKA_OUTGOING_PREFIX, defaultOutgoingChannel, outTriggers, Optional.of("String"), Optional.empty(), p -> !p.contains(".value."))
                    .ifPresent(result::add);
        }

        if (inputDefaultChannel.isPresent() && result.stream().noneMatch(e -> e.getChannelName().equals(defaultIncomingChannel) && e.isInputDefault())) {
            LOG.warn("No incoming channels found but default is defined {}", defaultIncomingChannel);
            buildChannelInfo(context, true, KAFKA_INCOMING_DEFAULT_CHANNEL, KAFKA_PREFIX, defaultIncomingChannel, inTriggers, Optional.of("String"), Optional.of(true), p -> true)
                    .ifPresent(result::add);
        }

        if (outputDefaultChannel.isPresent() && result.stream().noneMatch(e -> e.getChannelName().equals(defaultOutgoingChannel) && e.isOutputDefault())) {
            LOG.warn("No outgoing channels found but default is defined {}", defaultOutgoingChannel);
            buildChannelInfo(context, false, KAFKA_OUTGOING_DEFAULT_CHANNEL, KAFKA_PREFIX, defaultOutgoingChannel, outTriggers, Optional.of("String"), Optional.of(true), p -> true)
                    .ifPresent(result::add);
        }

        return result;
    }

    private static Collection<ChannelInfo> processQuarkus(KogitoBuildContext context) {
        Map<String, Collection<String>> inTriggers = new HashMap<>();
        Map<String, Collection<String>> outTriggers = new HashMap<>();

        for (String property : context.getApplicationProperties()) {
            if (property.startsWith(KOGITO_MESSAGING_INCOMING_TRIGGER)) {
                addTrigger(context, KOGITO_MESSAGING_INCOMING_TRIGGER, property, inTriggers);
            } else if (property.startsWith(KOGITO_MESSAGING_OUTGOING_TRIGGER)) {
                addTrigger(context, KOGITO_MESSAGING_OUTGOING_TRIGGER, property, outTriggers);
            }
        }

        Optional<String> inputDefaultChannel = context.getApplicationProperty(KOGITO_MESSAGING_INCOMING_DEFAULT_CHANNEL, String.class);
        Optional<String> outputDefaultChannel = context.getApplicationProperty(KOGITO_MESSAGING_OUTGOING_DEFAULT_CHANNEL, String.class);

        String defaultIncomingChannel = inputDefaultChannel.orElse(KogitoEventStreams.INCOMING);
        String defaultOutgoingChannel = outputDefaultChannel.orElse(KogitoEventStreams.OUTGOING);

        Collection<ChannelInfo> result = new ArrayList<>();
        for (String property : context.getApplicationProperties()) {
            buildChannelInfo(context, true, property, INCOMING_PREFIX, defaultIncomingChannel, inTriggers, Optional.empty(), Optional.empty(), p -> p.endsWith(".connector")).ifPresent(result::add);
            buildChannelInfo(context, false, property, OUTGOING_PREFIX, defaultOutgoingChannel, outTriggers, Optional.empty(), Optional.empty(), p -> p.endsWith(".connector")).ifPresent(result::add);
        }

        return result;
    }

    private static Optional<ChannelInfo> buildChannelInfo(KogitoBuildContext context,
            boolean input,
            String property,
            String prefix,
            String defaultChannel,
            Map<String, Collection<String>> triggers,
            Optional<String> typeProvided,
            Optional<Boolean> isDefault,
            Predicate<String> shouldProcessTest) {
        if (!property.startsWith(prefix) || !shouldProcessTest.test(property)) {
            return Optional.empty();
        }
        return Optional.of(getChannelInfo(context, property, prefix, input, defaultChannel, triggers, typeProvided, isDefault));

    }

    private static void addTrigger(KogitoBuildContext context, String prefix, String property, Map<String, Collection<String>> triggers) {
        String channelName = context.getApplicationProperty(property, String.class).orElse(null);
        String triggerName = property.substring(prefix.length());
        triggers.computeIfAbsent(channelName, ChannelMappingStrategy::initTriggers).add(triggerName);
    }

    private static Collection<String> initTriggers(String channelName) {
        Collection<String> result = new HashSet<>();
        result.add(channelName);
        return result;
    }

    private static ChannelInfo getChannelInfo(
            KogitoBuildContext context,
            String property,
            String prefix,
            boolean isInput,
            String defaultChannelName,
            Map<String, Collection<String>> triggers,
            Optional<String> typeProvided,
            Optional<Boolean> isDefault) {

        String channelName = getChannelName(property, prefix);
        LOG.debug("Creating channel name {} with triggers {} is input {} and defaultChannelName {}", channelName, triggers, isInput, defaultChannelName);
        String propertySerializerName = getPropertyName(prefix, channelName, "value." + (isInput ? "deserializer" : "serializer"));
        Optional<String> type = context.getApplicationProperty(propertySerializerName, String.class);
        String className = type.isPresent() ? getClassName(type) : typeProvided.orElse("Object");
        LOG.debug("Property serializer {} with value {} and className {}", propertySerializerName, type, className);
        return new ChannelInfo(
                channelName,
                context.getApplicationProperty(property).orElse(channelName),
                triggers.getOrDefault(channelName, Collections.singleton(channelName)),
                className,
                isInput,
                isDefault.orElse(channelName.equals(defaultChannelName)),
                context.getApplicationProperty((isInput ? UNMARSHALLLER_PREFIX : MARSHALLER_PREFIX) + channelName, String.class),
                cloudEventMode(context, channelName, property));
    }

    private static Optional<CloudEventMode> cloudEventMode(KogitoBuildContext context, String name, String property) {
        if (!context.getApplicationProperty("kogito.messaging.as-cloudevents", Boolean.class).orElse(true)) {
            return Optional.empty();
        }
        Optional<CloudEventMode> cloudEventMode = getCloudEventMode(context, CLOUD_EVENT_MODE + "." + name);
        if (cloudEventMode.isPresent()) {
            return cloudEventMode;
        }
        cloudEventMode = getCloudEventMode(context, CLOUD_EVENT_MODE);
        if (cloudEventMode.isPresent()) {
            return cloudEventMode;
        }
        // if no config, infer default from connector type
        return context.getApplicationProperty(property, String.class).map(ChannelMappingStrategy::toCloudEventMode);
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

    private static CloudEventMode toCloudEventMode(String connector) {
        return connector.equals("quarkus-http") ? CloudEventMode.BINARY : CloudEventMode.STRUCTURED;
    }

    private static Optional<CloudEventMode> getCloudEventMode(KogitoBuildContext context, String propName) {
        return context.getApplicationProperty(propName).map(String::toUpperCase).map(CloudEventMode::valueOf);
    }

    private static final String getPropertyName(String prefix, String name, String suffix) {
        return prefix + name + "." + suffix;
    }

    public static String getChannelName(String property, String prefix) {
        int idx = property.lastIndexOf('.');
        LOG.debug("processing property {} with prefix {} and idx {} and prefix length {}", property, prefix, idx, property.length());
        if (idx < 0) {
            idx = property.length();
        }
        if (idx <= prefix.length()) {
            idx = property.length();
        }
        return property.substring(prefix.length(), idx);
    }

}
