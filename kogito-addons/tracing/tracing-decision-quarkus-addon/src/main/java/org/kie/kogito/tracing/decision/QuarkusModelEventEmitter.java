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

import javax.inject.Inject;
import javax.inject.Singleton;

import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.kie.kogito.decision.DecisionModelResourcesProvider;
import org.reactivestreams.Publisher;

import io.smallrye.mutiny.operators.multi.processors.BroadcastProcessor;

@Singleton
public class QuarkusModelEventEmitter extends BaseModelEventEmitter {

    private final BroadcastProcessor<String> eventSubject;

    @Inject
    public QuarkusModelEventEmitter(final DecisionModelResourcesProvider decisionModelResourcesProvider) {
        super(decisionModelResourcesProvider);
        this.eventSubject = BroadcastProcessor.create();
    }

    @Override
    public void publishDecisionModels() {
        super.publishDecisionModels();
    }

    @Outgoing("kogito-tracing-model")
    public Publisher<String> getEventPublisher() {
        return eventSubject.toHotStream();
    }

    @Override
    public void emit(final String payload) {
        eventSubject.onNext(payload);
    }
}
