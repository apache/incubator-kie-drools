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
package org.kie.kogito.tracing.decision;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * This class must always extend <code>org.kie.kogito.tracing.decision.DecisionTracingListener</code>
 * for code generation plugins to correctly detect if this addon is enabled.
 */
@Component
public final class SpringBootDecisionTracingListener extends DecisionTracingListener {

    @Autowired
    public SpringBootDecisionTracingListener(
            ApplicationEventPublisher eventPublisher,
            SpringBootDecisionTracingCollector collector,
            @Value(value = "${kogito.addon.tracing.decision.asyncEnabled:true}") boolean asyncEnabled) {
        if (asyncEnabled) {
            setEventConsumer(eventPublisher::publishEvent);
        } else {
            setEventConsumer(collector::onApplicationEvent);
        }
    }

}
