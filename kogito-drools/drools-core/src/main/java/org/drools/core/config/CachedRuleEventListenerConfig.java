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

import java.util.ArrayList;
import java.util.List;

import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.RuleRuntimeEventListener;
import org.kie.kogito.rules.RuleEventListenerConfig;

public class CachedRuleEventListenerConfig implements RuleEventListenerConfig {

    private final List<AgendaEventListener> agendaEventListeners = new ArrayList<>();
    private final List<RuleRuntimeEventListener> ruleRuntimeListeners = new ArrayList<>();

    public CachedRuleEventListenerConfig register(AgendaEventListener listener) {
        agendaEventListeners.add(listener);
        return this;
    }
    
    public CachedRuleEventListenerConfig register(RuleRuntimeEventListener listener) {
        ruleRuntimeListeners.add(listener);
        return this;
    }
    
    @Override
    public List<AgendaEventListener> agendaListeners() {
        return agendaEventListeners;
    }

    @Override
    public List<RuleRuntimeEventListener> ruleRuntimeListeners() {        
        return ruleRuntimeListeners;
    }

}
