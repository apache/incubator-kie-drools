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

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import org.kie.dmn.api.core.DMNMetadata;
import org.kie.dmn.api.core.event.AfterEvaluateBKMEvent;
import org.kie.dmn.api.core.event.BeforeEvaluateBKMEvent;
import org.kie.dmn.api.core.event.DMNEvent;
import org.kie.dmn.api.core.event.DMNRuntimeEventListener;
import org.kie.kogito.tracing.decision.event.evaluate.EvaluateEvent;

import static org.kie.kogito.explainability.Constants.SKIP_TRACING;

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
        commonHandleEvent(EvaluateEvent::from, event);
    }

    @Override
    public void afterEvaluateAll(org.kie.dmn.api.core.event.AfterEvaluateAllEvent event) {
        commonHandleEvent(EvaluateEvent::from, event);
    }

    @Override
    public void beforeEvaluateDecision(org.kie.dmn.api.core.event.BeforeEvaluateDecisionEvent event) {
        commonHandleEvent(EvaluateEvent::from, event);
    }

    @Override
    public void afterEvaluateDecision(org.kie.dmn.api.core.event.AfterEvaluateDecisionEvent event) {
        commonHandleEvent(EvaluateEvent::from, event);
    }

    @Override
    public void beforeEvaluateContextEntry(org.kie.dmn.api.core.event.BeforeEvaluateContextEntryEvent event) {
        commonHandleEvent(EvaluateEvent::from, event);
    }

    @Override
    public void afterEvaluateContextEntry(org.kie.dmn.api.core.event.AfterEvaluateContextEntryEvent event) {
        commonHandleEvent(EvaluateEvent::from, event);
    }

    @Override
    public void beforeEvaluateDecisionTable(org.kie.dmn.api.core.event.BeforeEvaluateDecisionTableEvent event) {
        commonHandleEvent(EvaluateEvent::from, event);
    }

    @Override
    public void afterEvaluateDecisionTable(org.kie.dmn.api.core.event.AfterEvaluateDecisionTableEvent event) {
        commonHandleEvent(EvaluateEvent::from, event);
    }

    @Override
    public void beforeEvaluateDecisionService(org.kie.dmn.api.core.event.BeforeEvaluateDecisionServiceEvent event) {
        commonHandleEvent(EvaluateEvent::from, event);
    }

    @Override
    public void afterEvaluateDecisionService(org.kie.dmn.api.core.event.AfterEvaluateDecisionServiceEvent event) {
        commonHandleEvent(EvaluateEvent::from, event);
    }

    @Override
    public void beforeEvaluateBKM(BeforeEvaluateBKMEvent event) {
        commonHandleEvent(EvaluateEvent::from, event);
    }

    @Override
    public void afterEvaluateBKM(AfterEvaluateBKMEvent event) {
        commonHandleEvent(EvaluateEvent::from, event);
    }

    private <T extends DMNEvent> void commonHandleEvent(Function<T, EvaluateEvent> factory, T event) {
        if (!isTracingEnabled(event.getResult().getContext().getMetadata())) {
            eventConsumer.accept(factory.apply(event));
        }
    }

    private <T extends DMNEvent> boolean isTracingEnabled(DMNMetadata metadata) {
        Optional<Boolean> skipTracing = Optional.ofNullable((Boolean) metadata.get(SKIP_TRACING));
        return skipTracing.orElse(false);
    }

}
