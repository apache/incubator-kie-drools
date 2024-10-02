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
package org.kie.kogito.codegen.rules.config;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.drools.ruleunits.api.conf.ClockType;
import org.drools.ruleunits.api.conf.EventProcessingType;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.rules.RuleUnitConfig;

public final class NamedRuleUnitConfig {

    private static String CONFIG_PREFIX = "kogito.rules.";
    /**
     * (string) kind of event processing type for a given rule-unit; possible values: CLOUD, STREAM; default is null
     */
    private static String CONFIG_EVENT_PROCESSING_TYPE = CONFIG_PREFIX + "\"%s\".event-processing-type";
    /**
     * (string) kind of event clock type for a given rule-unit; possible values: PSEUDO, REALTIME; default is null
     */
    private static String CONFIG_CLOCK_TYPE = CONFIG_PREFIX + "\"%s\".clock-type";
    /**
     * (integer) size of session poolfor a given rule-unit; possible values; default is null
     */
    private static String CONFIG_SESSIONS_POOL = CONFIG_PREFIX + "\"%s\".sessions-pool";

    public static List<NamedRuleUnitConfig> fromContext(KogitoBuildContext context) {
        HashSet<String> canonicalNames = new HashSet<>();
        for (String k : context.getApplicationProperties()) {
            if (k.startsWith(CONFIG_PREFIX)) {
                String rest = k.substring(CONFIG_PREFIX.length());
                Optional<String> unitCanonicalName = parseQuotedIdentifier(rest);
                unitCanonicalName.ifPresent(canonicalNames::add);
            }
        }

        ArrayList<NamedRuleUnitConfig> configs = new ArrayList<>();
        for (String canonicalName : canonicalNames) {
            EventProcessingType eventProcessingType = context.getApplicationProperty(
                    String.format(CONFIG_EVENT_PROCESSING_TYPE, canonicalName))
                    .map(String::toUpperCase)
                    .map(EventProcessingType::valueOf)
                    .orElse(null);

            ClockType clockType = context.getApplicationProperty(
                    String.format(CONFIG_CLOCK_TYPE, canonicalName))
                    .map(String::toUpperCase)
                    .map(ClockType::valueOf)
                    .orElse(null);

            Optional<String> sp = context.getApplicationProperty(
                    String.format(CONFIG_SESSIONS_POOL, canonicalName));
            Integer sessionPool = sp.map(Integer::parseInt).orElse(null);

            configs.add(new NamedRuleUnitConfig(
                    canonicalName,
                    new RuleUnitConfig(
                            eventProcessingType,
                            clockType,
                            sessionPool)));
        }

        return configs;
    }

    private static Optional<String> parseQuotedIdentifier(String key) {
        if (key.startsWith("\"")) {
            int endIndex = key.substring(1).indexOf('"');
            if (endIndex == -1) {
                return Optional.empty();
            } else {
                return Optional.of(key.substring(1, endIndex + 1));
            }
        } else {
            return Optional.empty();
        }
    }

    private final String canonicalName;
    private final RuleUnitConfig config;

    public NamedRuleUnitConfig(String canonicalName, RuleUnitConfig config) {
        this.canonicalName = canonicalName;
        this.config = config;
    }

    public String getCanonicalName() {
        return canonicalName;
    }

    public RuleUnitConfig getConfig() {
        return config;
    }
}
