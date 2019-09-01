/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.core.config;

import org.kie.api.conf.EventProcessingOption;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.kogito.rules.RuleConfig;
import org.kie.kogito.rules.RuleEventListenerConfig;

public class StaticRuleConfig implements RuleConfig {
    
    private final RuleEventListenerConfig ruleEventListenerConfig;
    private final EventProcessingOption eventProcessing;
    private final ClockTypeOption clockType;

    public StaticRuleConfig(RuleEventListenerConfig ruleEventListenerConfig, EventProcessingOption eventProcessing, ClockTypeOption clockType) {
        this.ruleEventListenerConfig = ruleEventListenerConfig;
        this.eventProcessing = eventProcessing;
        this.clockType = clockType;
    }

    public StaticRuleConfig(RuleEventListenerConfig ruleEventListenerConfig) {
        this(ruleEventListenerConfig, EventProcessingOption.CLOUD, ClockTypeOption.REALTIME);
    }

    @Override
    public RuleEventListenerConfig ruleEventListeners() {
        return ruleEventListenerConfig;
    }

    @Override
    public EventProcessingOption eventProcessingMode() {
        return eventProcessing;
    }

    @Override
    public ClockTypeOption clockType() {
        return clockType;
    }
}
