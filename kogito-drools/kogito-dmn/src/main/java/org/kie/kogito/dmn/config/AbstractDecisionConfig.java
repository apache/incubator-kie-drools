/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.dmn.config;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.kie.dmn.api.core.event.DMNRuntimeEventListener;
import org.kie.kogito.decision.DecisionEventListenerConfig;

import static java.util.stream.Collectors.toList;

public abstract class AbstractDecisionConfig implements org.kie.kogito.decision.DecisionConfig {

    private final DecisionEventListenerConfig decisionEventListener;

    protected AbstractDecisionConfig(
            Iterable<DecisionEventListenerConfig> decisionEventListenerConfigs,
            Iterable<DMNRuntimeEventListener> dmnRuntimeEventListeners) {
        this.decisionEventListener = extractDecisionEventListenerConfig(decisionEventListenerConfigs, dmnRuntimeEventListeners);
    }

    @Override
    public DecisionEventListenerConfig decisionEventListeners() {
        return decisionEventListener;
    }

    private DecisionEventListenerConfig extractDecisionEventListenerConfig(
            Iterable<DecisionEventListenerConfig> decisionEventListenerConfigs,
            Iterable<DMNRuntimeEventListener> dmnRuntimeEventListeners) {
        return this.mergeDecisionEventListenerConfig(
                StreamSupport.stream(decisionEventListenerConfigs.spliterator(), false)
                        .collect(toList()),
                StreamSupport.stream(dmnRuntimeEventListeners.spliterator(), false)
                        .collect(toList()));
    }

    private DecisionEventListenerConfig mergeDecisionEventListenerConfig(
            java.util.Collection<DecisionEventListenerConfig> decisionEventListenerConfigs,
            Collection<DMNRuntimeEventListener> dmnRuntimeEventListeners) {
        return new org.kie.kogito.dmn.config.CachedDecisionEventListenerConfig(merge(decisionEventListenerConfigs, DecisionEventListenerConfig::listeners, dmnRuntimeEventListeners));
    }

    private static <C, L> List<L> merge(Collection<C> configs, Function<C, Collection<L>> configToListeners, Collection<L> listeners) {
        return Stream.concat(
                configs.stream().flatMap(c -> configToListeners.apply(c).stream()),
                listeners.stream()).collect(toList());
    }
}
