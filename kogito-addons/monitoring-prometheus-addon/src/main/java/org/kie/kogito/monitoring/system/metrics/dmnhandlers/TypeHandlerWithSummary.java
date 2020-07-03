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

package org.kie.kogito.monitoring.system.metrics.dmnhandlers;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Summary;

public interface TypeHandlerWithSummary<T> extends TypeHandler<T> {
    default Summary initializeDefaultSummary(String dmnType, CollectorRegistry registry) {
        Summary.Builder builder = Summary.build() // Calculate quantiles over a sliding window of time - default = 10 minutes
                .quantile(0.1, 0.01)   // Add 10th percentile with 1% tolerated error
                .quantile(0.25, 0.05)
                .quantile(0.50, 0.05)   // Add 50th percentile (= median) with 5% tolerated error
                .quantile(0.75, 0.05)
                .quantile(0.9, 0.05)
                .quantile(0.99, 0.01)
                .name(dmnType.replace(" ", "_") + DecisionConstants.DECISIONS_NAME_SUFFIX)
                .help(DecisionConstants.DECISIONS_HELP)
                .labelNames(DecisionConstants.DECISION_ENDPOINT_LABELS);
        return registry == null ? builder.register(CollectorRegistry.defaultRegistry) : builder.register(registry);
    }
}
