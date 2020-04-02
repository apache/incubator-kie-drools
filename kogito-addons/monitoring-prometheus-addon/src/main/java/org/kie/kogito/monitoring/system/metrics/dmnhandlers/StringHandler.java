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
import io.prometheus.client.Counter;

public class StringHandler implements TypeHandler<String> {

    private final Counter counter;

    private String dmnType;

    public StringHandler(String dmnType, CollectorRegistry registry) {
        this.dmnType = dmnType;
        this.counter = initializeCounter(dmnType, registry);
    }

    public StringHandler(String dmnType) {
        this(dmnType, null);
    }

    @Override
    public void record(String decision, String endpointName, String sample) {

        counter.labels(decision, endpointName, sample).inc();
    }

    @Override
    public String getDmnType() {
        return dmnType;
    }

    private Counter initializeCounter(String dmnType, CollectorRegistry registry) {
        Counter.Builder builder = Counter.build().name(dmnType + DecisionConstants.DECISIONS_NAME_SUFFIX)
                .help(DecisionConstants.DECISIONS_HELP)
                .labelNames(DecisionConstants.DECISION_ENDPOINT_IDENTIFIER_LABELS);

        return registry == null ? builder.register(CollectorRegistry.defaultRegistry) : builder.register(registry);
    }
}
