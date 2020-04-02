/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.monitoring.system.metrics;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kie.dmn.api.core.DMNDecisionResult;
import org.kie.kogito.dmn.rest.DMNResult;
import org.kie.kogito.grafana.dmn.SupportedDecisionTypes;
import org.kie.kogito.monitoring.system.metrics.dmnhandlers.BigDecimalHandler;
import org.kie.kogito.monitoring.system.metrics.dmnhandlers.BooleanHandler;
import org.kie.kogito.monitoring.system.metrics.dmnhandlers.StringHandler;
import org.kie.kogito.monitoring.system.metrics.dmnhandlers.TypeHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DMNResultMetricsBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(DMNResultMetricsBuilder.class);

    private static final Map<Class, TypeHandler> handlers = generateHandlers();

    private DMNResultMetricsBuilder() {
    }

    private static Map<Class, TypeHandler> generateHandlers() {
        HashMap<Class, TypeHandler> handlers = new HashMap<>();
        handlers.put(String.class, new StringHandler(SupportedDecisionTypes.fromInternalToStandard(String.class)));
        handlers.put(Boolean.class, new BooleanHandler(SupportedDecisionTypes.fromInternalToStandard(Boolean.class)));
        handlers.put(BigDecimal.class, new BigDecimalHandler(SupportedDecisionTypes.fromInternalToStandard(BigDecimal.class)));
        return handlers;
    }

    public static Map<Class, TypeHandler> getHandlers() {
        return handlers;
    }

    public static void generateMetrics(DMNResult dmnResult, String endpointName) {
        if (dmnResult == null) {
            LOGGER.warn("DMNResultMetricsBuilder can't register the metrics because the dmn result is null.");
            return;
        }

        List<DMNDecisionResult> decisionResults = dmnResult.getDecisionResults();
        for (DMNDecisionResult decision : decisionResults) {
            Object result = decision.getResult();
            if (result != null && SupportedDecisionTypes.isSupported(result.getClass())) {
                handlers.get(result.getClass()).record(decision.getDecisionName(), endpointName, result);
            }
        }
    }
}
