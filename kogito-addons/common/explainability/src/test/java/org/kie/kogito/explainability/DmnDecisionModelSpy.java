/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.explainability;

import java.util.ArrayList;
import java.util.List;

import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.kogito.ExecutionIdSupplier;
import org.kie.kogito.dmn.DmnDecisionModel;

import static org.kie.kogito.explainability.Constants.SKIP_MONITORING;

public class DmnDecisionModelSpy extends DmnDecisionModel {

    private final List<Boolean> evaluationSkipMonitoringHistory = new ArrayList<>();

    public DmnDecisionModelSpy(DMNRuntime dmnRuntime, String namespace, String name, ExecutionIdSupplier execIdSupplier) {
        super(dmnRuntime, namespace, name, execIdSupplier);
    }

    @Override
    public DMNResult evaluateAll(DMNContext context) {
        evaluationSkipMonitoringHistory.add((boolean) context.getMetadata().asMap().getOrDefault(SKIP_MONITORING, false));
        return super.evaluateAll(context);
    }

    List<Boolean> getEvaluationSkipMonitoringHistory() {
        return evaluationSkipMonitoringHistory;
    }
}
