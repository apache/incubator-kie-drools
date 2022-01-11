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
package org.kie.kogito.monitoring.core.common.decision;

import java.util.Map;

import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNMetadata;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.FEELPropertyAccessible;
import org.kie.kogito.KogitoGAV;
import org.kie.kogito.decision.DecisionModel;
import org.kie.kogito.monitoring.core.common.system.metrics.DMNResultMetricsBuilder;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Metrics;

import static org.kie.kogito.monitoring.core.common.Constants.SKIP_MONITORING;

public class MonitoredDecisionModel implements DecisionModel {

    private final DecisionModel originalModel;
    private final DMNResultMetricsBuilder dmnResultMetricsBuilder;

    public MonitoredDecisionModel(DecisionModel originalModel, KogitoGAV gav, MeterRegistry meterRegistry) {
        this.originalModel = originalModel;
        this.dmnResultMetricsBuilder = new DMNResultMetricsBuilder(gav, meterRegistry);
    }

    public MonitoredDecisionModel(DecisionModel originalModel, KogitoGAV gav) {
        this(originalModel, gav, Metrics.globalRegistry);
    }

    protected MonitoredDecisionModel(DecisionModel originalModel, DMNResultMetricsBuilder dmnResultMetricsBuilder) {
        this.originalModel = originalModel;
        this.dmnResultMetricsBuilder = dmnResultMetricsBuilder;
    }

    @Override
    public DMNContext newContext(Map<String, Object> inputSet) {
        return originalModel.newContext(inputSet);
    }

    @Override
    public DMNContext newContext(FEELPropertyAccessible inputSet) {
        return originalModel.newContext(inputSet);
    }

    @Override
    public DMNResult evaluateAll(DMNContext context) {
        DMNResult result = originalModel.evaluateAll(context);
        if (!shouldSkipMonitoring(context.getMetadata())) {
            dmnResultMetricsBuilder.generateMetrics(result, originalModel.getDMNModel().getName());
        }
        return result;
    }

    @Override
    public DMNResult evaluateDecisionService(DMNContext context, String decisionServiceName) {
        DMNResult result = originalModel.evaluateDecisionService(context, decisionServiceName);
        if (!shouldSkipMonitoring(context.getMetadata())) {
            dmnResultMetricsBuilder.generateMetrics(result, originalModel.getDMNModel().getName());
        }
        return result;
    }

    @Override
    public DMNModel getDMNModel() {
        return originalModel.getDMNModel();
    }

    private boolean shouldSkipMonitoring(DMNMetadata dmnMetadata) {
        return dmnMetadata != null && (boolean) dmnMetadata.asMap().getOrDefault(SKIP_MONITORING, false);
    }
}
