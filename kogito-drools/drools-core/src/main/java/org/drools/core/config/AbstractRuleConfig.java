/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.RuleRuntimeEventListener;
import org.kie.kogito.rules.RuleConfig;
import org.kie.kogito.rules.RuleEventListenerConfig;

public abstract class AbstractRuleConfig implements RuleConfig {

    private final RuleEventListenerConfig ruleEventListenerConfig;

    public AbstractRuleConfig(RuleEventListenerConfig ruleEventListenerConfig) {
        this.ruleEventListenerConfig = ruleEventListenerConfig;
    }

    public AbstractRuleConfig(
            Iterable<RuleEventListenerConfig> ruleEventListenerConfigs,
            Iterable<AgendaEventListener> agendaEventListeners,
            Iterable<RuleRuntimeEventListener> ruleRuntimeEventListeners) {
        this.ruleEventListenerConfig = extractRuleEventListenerConfig(
                ruleEventListenerConfigs, agendaEventListeners, ruleRuntimeEventListeners);
    }

    @Override
    public RuleEventListenerConfig ruleEventListeners() {
        return ruleEventListenerConfig;
    }

    private RuleEventListenerConfig extractRuleEventListenerConfig(
            Iterable<RuleEventListenerConfig> ruleEventListenerConfigs,
            Iterable<AgendaEventListener> agendaEventListeners,
            Iterable<RuleRuntimeEventListener> ruleRuntimeEventListeners) {
        return this.mergeRuleEventListenerConfig(
                StreamSupport.stream(ruleEventListenerConfigs.spliterator(), false)
                        .collect(Collectors.toList()),
                StreamSupport.stream(agendaEventListeners.spliterator(), false)
                        .collect(Collectors.toList()),
                StreamSupport.stream(ruleRuntimeEventListeners.spliterator(), false)
                        .collect(Collectors.toList()));
    }

    private RuleEventListenerConfig mergeRuleEventListenerConfig(
            Collection<RuleEventListenerConfig> ruleEventListenerConfigs,
            Collection<AgendaEventListener> agendaEventListeners,
            Collection<RuleRuntimeEventListener> ruleRuntimeEventListeners) {
        return new CachedRuleEventListenerConfig(
                merge(ruleEventListenerConfigs,
                      RuleEventListenerConfig::agendaListeners,
                      agendaEventListeners),
                merge(ruleEventListenerConfigs,
                      RuleEventListenerConfig::ruleRuntimeListeners,
                      ruleRuntimeEventListeners));
    }

    private static <C, L> List<L> merge(Collection<C> configs, Function<C, Collection<L>> configToListeners, Collection<L> listeners) {
        return Stream.concat(
                configs.stream().flatMap(c -> configToListeners.apply(c).stream()), listeners.stream())
                .collect(Collectors.toList());
    }
}
