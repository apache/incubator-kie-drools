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

package org.kie.kogito.tracing.decision.explainability;

import java.util.Collection;

import org.kie.kogito.Application;
import org.kie.kogito.tracing.decision.event.explainability.PredictInput;
import org.kie.kogito.tracing.decision.event.explainability.PredictOutput;

import static java.util.Collections.singletonList;

public class ExplainabilityService {

    public static ExplainabilityService INSTANCE = new ExplainabilityService(singletonList(new DecisionExplainabilityResourceExecutor()));

    private Collection<ExplainabilityResourceExecutor> executors;

    public ExplainabilityService(Collection<ExplainabilityResourceExecutor> executors) {
        this.executors = executors;
    }

    public PredictOutput processRequest(Application application, PredictInput predictInput) {
        return executors.stream()
                .filter(r -> r.acceptRequest(predictInput))
                .map(r -> r.processRequest(application, predictInput))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Malformed resourceType " + predictInput.getModelIdentifier().getResourceType()));
    }
}
