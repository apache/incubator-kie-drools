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

import java.util.Arrays;
import java.util.List;

import io.cloudevents.CloudEvent;
import io.reactivex.subscribers.TestSubscriber;
import org.junit.jupiter.api.Test;
import org.kie.api.management.GAV;
import org.kie.internal.decision.DecisionModelResource;
import org.kie.internal.decision.DecisionModelResourcesProvider;
import org.kie.kogito.decision.DecisionModelType;
import org.kie.kogito.tracing.decision.event.CloudEventUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class QuarkusModelEventEmitterTest {

    @Test
    public void testEmitEvent() {
        final TestSubscriber<String> subscriber = new TestSubscriber<>();
        final List<DecisionModelResource> models = Arrays.asList(makeModel(), makeModel());
        final DecisionModelResourcesProvider mockedDecisionModelResourcesProvider = () -> models;

        final QuarkusModelEventEmitter eventEmitter = new QuarkusModelEventEmitter(mockedDecisionModelResourcesProvider);
        eventEmitter.getEventPublisher().subscribe(subscriber);
        eventEmitter.publishDecisionModels();

        subscriber.assertValueCount(2);
        final String rawCloudEvent1 = subscriber.values().get(0);
        final String rawCloudEvent2 = subscriber.values().get(1);
        final CloudEvent cloudEvent1 = CloudEventUtils.decode(rawCloudEvent1).orElseThrow(IllegalStateException::new);
        final CloudEvent cloudEvent2 = CloudEventUtils.decode(rawCloudEvent2).orElseThrow(IllegalStateException::new);

        assertEquals("id", cloudEvent1.getId());
        assertEquals("id", cloudEvent2.getId());
    }

    private DecisionModelResource makeModel() {
        final DecisionModelResource model = mock(DecisionModelResource.class);
        when(model.getGav()).thenReturn(new GAV("groupId", "artifactId", "version"));
        when(model.getModelName()).thenReturn("name");
        when(model.getNamespace()).thenReturn("namespace");
        when(model.getModelType()).thenReturn(DecisionModelType.DMN);
        when(model.get()).thenReturn("model");
        return model;
    }
}
