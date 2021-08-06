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
package org.kie.kogito.codegen.process;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.jbpm.compiler.canonical.TriggerMetaData;
import org.jbpm.compiler.canonical.TriggerMetaData.TriggerType;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultChannelMappingStrategy implements ChannelMappingStrategy {

    private static final Logger logger = LoggerFactory.getLogger(DefaultChannelMappingStrategy.class);

    private static final String OUTGOING_PREFIX = "mp.messaging.outgoing.";
    private static final String INCOMING_PREFIX = "mp.messaging.incoming.";

    @Override
    public Map<TriggerMetaData, String> getChannelMapping(KogitoBuildContext context,
            Collection<TriggerMetaData> metadata) {
        Map<TriggerMetaData, String> map = new LinkedHashMap<>();
        Set<TriggerMetaData> missingTriggers = new HashSet<>();
        for (TriggerMetaData trigger : metadata) {
            if (trigger.getType() == TriggerType.ConsumeMessage || trigger.getType() == TriggerType.ProduceMessage) {
                String channel = getChannel(trigger, context);
                if (channel != null) {
                    map.put(trigger, channel);
                } else {
                    missingTriggers.add(trigger);
                }
            } else {
                logger.debug("trigger {} is not consumer, not producer, ignoring", trigger);
            }
        }
        if (!missingTriggers.isEmpty()) {
            handleMissing(map, missingTriggers);
        }
        return map;
    }

    protected void handleMissing(Map<TriggerMetaData, String> map, Collection<TriggerMetaData> missingTriggers) {
        logger.warn("There is no mapping for these triggers {}, using default", missingTriggers);
    }

    private String getChannel(TriggerMetaData trigger, KogitoBuildContext context) {
        if (trigger.getType() == TriggerType.ConsumeMessage) {
            return getChannel(trigger, context, INCOMING_PREFIX);
        } else {
            return getChannel(trigger, context, OUTGOING_PREFIX);
        }
    }

    private static String getChannel(TriggerMetaData trigger, KogitoBuildContext context, String prefix) {
        if (context.getApplicationProperty(getPropertyName(prefix, trigger.getName())).isPresent()) {
            return trigger.getName();
        } else {
            return null;
        }
    }

    private static final String getPropertyName(String prefix, String name) {
        return prefix + name + ".connector";
    }
}
