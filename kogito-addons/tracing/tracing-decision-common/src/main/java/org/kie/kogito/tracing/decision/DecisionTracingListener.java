/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.tracing.decision;

import java.util.function.Consumer;

import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.event.DMNRuntimeEventListener;
import org.kie.kogito.decision.DecisionExecutionIdUtils;
import org.kie.kogito.tracing.decision.event.AfterEvaluateAllEvent;
import org.kie.kogito.tracing.decision.event.BeforeEvaluateAllEvent;
import org.kie.kogito.tracing.decision.event.EvaluateEvent;

/**
 * This class must always have exact FQCN as <code>org.kie.kogito.tracing.decision.DecisionTracingListener</code>
 * for code generation plugins to correctly detect if this addon is enabled.
 */
public class DecisionTracingListener implements DMNRuntimeEventListener {

    private Consumer<EvaluateEvent> eventConsumer;

    public DecisionTracingListener(Consumer<EvaluateEvent> eventConsumer) {
        this.eventConsumer = eventConsumer;
    }

    protected DecisionTracingListener() {
    }

    protected void setEventConsumer(Consumer<EvaluateEvent> eventConsumer) {
        this.eventConsumer = eventConsumer;
    }

    @Override
    public void beforeEvaluateAll(org.kie.dmn.api.core.event.BeforeEvaluateAllEvent event) {
        eventConsumer.accept(new BeforeEvaluateAllEvent(
                extractExecutionId(event.getResult().getContext()),
                event.getModelName(),
                event.getModelNamespace(),
                event.getResult().getContext()
        ));
    }

    @Override
    public void afterEvaluateAll(org.kie.dmn.api.core.event.AfterEvaluateAllEvent event) {
        eventConsumer.accept(new AfterEvaluateAllEvent(
                extractExecutionId(event.getResult().getContext()),
                event.getModelName(),
                event.getModelNamespace(),
                event.getResult()
        ));
    }

    private String extractExecutionId(DMNContext context) {
        return DecisionExecutionIdUtils.get(context);
    }

}
