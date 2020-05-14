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

package org.kie.kogito.tracing.decision.aggregator;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

import io.cloudevents.v1.CloudEventBuilder;
import io.cloudevents.v1.CloudEventImpl;
import org.kie.kogito.tracing.decision.event.AfterEvaluateAllEvent;
import org.kie.kogito.tracing.decision.event.EvaluateEvent;

public class DefaultAggregator implements Aggregator<AfterEvaluateAllEvent> {

    @Override
    public CloudEventImpl<AfterEvaluateAllEvent> aggregate(String evaluationId, List<EvaluateEvent> events) {
        AfterEvaluateAllEvent event = Optional.ofNullable(events)
                .filter(l -> !l.isEmpty())
                .map(l -> l.get(l.size() - 1))
                .filter(AfterEvaluateAllEvent.class::isInstance)
                .map(AfterEvaluateAllEvent.class::cast)
                .orElseThrow(() -> new IllegalStateException("Invalid event list"));

        return CloudEventBuilder.<AfterEvaluateAllEvent>builder()
                .withType(AfterEvaluateAllEvent.class.getName())
                .withId(evaluationId)
                .withSource(URI.create(URLEncoder.encode(event.getModelName(), StandardCharsets.UTF_8)))
                .withData(event)
                .build();
    }

}
