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
package org.kie.kogito.addon.cloudevents.quarkus.deployment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;

public class ChannelMappingStrategy {

    private ChannelMappingStrategy() {
    }

    private static final String OUTGOING_PREFIX = "mp.messaging.outgoing.";
    private static final String INCOMING_PREFIX = "mp.messaging.incoming.";

    private static Config config = ConfigProvider.getConfig();

    public static Collection<ChannelInfo> getChannelMapping() {
        Collection<ChannelInfo> result = new ArrayList<>();
        for (String property : config.getPropertyNames()) {
            if (property.startsWith(INCOMING_PREFIX) && property.endsWith(".connector")) {
                result.add(getChannelInfo(property, INCOMING_PREFIX, true));
            } else if (property.startsWith(OUTGOING_PREFIX) && property.endsWith(".connector")) {
                result.add(getChannelInfo(property, OUTGOING_PREFIX, false));
            }
        }
        return result;
    }

    private static ChannelInfo getChannelInfo(String property, String prefix, boolean isInput) {
        String name = extractChannelName(prefix, property);
        return new ChannelInfo(name, getClassName(config.getOptionalValue(getPropertyName(prefix, name, "value." + (isInput ? "deserializer" : "serializer")), String.class)), isInput);
    }

    private static String extractChannelName(String prefix, String property) {
        return property.substring(prefix.length(), property.lastIndexOf('.'));
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
