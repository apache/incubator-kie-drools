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

import javax.inject.Singleton;

import io.quarkus.vertx.ConsumeEvent;
import io.reactivex.BackpressureStrategy;
import io.reactivex.subjects.PublishSubject;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.kie.kogito.tracing.decision.event.AfterEvaluateAllEvent;
import org.kie.kogito.tracing.decision.event.BeforeEvaluateAllEvent;
import org.reactivestreams.Publisher;

@Singleton
public class KogitoDecisionTracingCollector {

    private final PublishSubject<String> eventSubject;
    private final DecisionTracingCollector collector;

    public KogitoDecisionTracingCollector() {
        eventSubject = PublishSubject.create();
        collector = new DecisionTracingCollector(eventSubject::onNext);
    }

    @Outgoing("kogito-tracing-decision")
    public Publisher<String> getEventPublisher() {
        return eventSubject.toFlowable(BackpressureStrategy.BUFFER);
    }

    @ConsumeEvent("kogito-tracing-decision_BeforeEvaluateAllEvent")
    public void onEvent(BeforeEvaluateAllEvent event) {
        collector.addEvent(event);
    }

    @ConsumeEvent("kogito-tracing-decision_AfterEvaluateAllEvent")
    public void onEvent(AfterEvaluateAllEvent event) {
        collector.addEvent(event);
    }

}
