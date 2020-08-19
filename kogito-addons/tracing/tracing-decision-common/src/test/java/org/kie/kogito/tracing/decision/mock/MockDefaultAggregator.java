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

package org.kie.kogito.tracing.decision.mock;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.cloudevents.v1.CloudEventImpl;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.feel.util.Pair;
import org.kie.kogito.conf.ConfigBean;
import org.kie.kogito.tracing.decision.aggregator.DefaultAggregator;
import org.kie.kogito.tracing.decision.event.evaluate.EvaluateEvent;
import org.kie.kogito.tracing.decision.event.trace.TraceEvent;

public class MockDefaultAggregator extends DefaultAggregator {

    final Map<String, Pair<List<EvaluateEvent>, CloudEventImpl<TraceEvent>>> calls = new HashMap<>();

    public Map<String, Pair<List<EvaluateEvent>, CloudEventImpl<TraceEvent>>> getCalls() {
        return calls;
    }

    @Override
    public CloudEventImpl<TraceEvent> aggregate(DMNModel model, String executionId, List<EvaluateEvent> events, ConfigBean configBean) {
        CloudEventImpl<TraceEvent> result = super.aggregate(model, executionId, events, configBean);
        calls.put(executionId, new Pair<>(events, result));
        return result;
    }

}
