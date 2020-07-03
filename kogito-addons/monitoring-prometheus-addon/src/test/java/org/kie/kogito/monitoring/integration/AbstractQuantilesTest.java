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

package org.kie.kogito.monitoring.integration;

import io.prometheus.client.Collector;
import io.prometheus.client.CollectorRegistry;
import org.kie.kogito.monitoring.system.metrics.dmnhandlers.DecisionConstants;
import org.kie.kogito.monitoring.system.metrics.dmnhandlers.TypeHandler;

public abstract class AbstractQuantilesTest<T extends TypeHandler> {

    public static final String[] INTERNAL_PROMETHEUS_LABELS =
            new String[]{
                    DecisionConstants.DECISION_ENDPOINT_IDENTIFIER_LABELS[0],
                    DecisionConstants.DECISION_ENDPOINT_IDENTIFIER_LABELS[1],
                    "quantile"
            };
    protected static final String ENDPOINT_NAME = "hello";
    protected CollectorRegistry registry;
    protected T handler;

    protected double getQuantile(String decision, String name, String labelValue, double q) {
        return registry.getSampleValue(name, INTERNAL_PROMETHEUS_LABELS, new String[]{decision, labelValue, Collector.doubleToGoString(q)}).doubleValue();
    }
}
