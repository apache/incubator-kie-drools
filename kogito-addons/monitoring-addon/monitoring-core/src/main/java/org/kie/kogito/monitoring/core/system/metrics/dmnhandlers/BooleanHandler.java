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

package org.kie.kogito.monitoring.core.system.metrics.dmnhandlers;

import java.util.Arrays;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import org.kie.kogito.monitoring.core.MonitoringRegistry;

public class BooleanHandler implements TypeHandler<Boolean> {

    private final String dmnType;

    private final MeterRegistry meterRegistry;

    public BooleanHandler(String dmnType) {
        this(dmnType, MonitoringRegistry.getDefaultMeterRegistry());
    }

    public BooleanHandler(String dmnType, MeterRegistry registry) {
        this.dmnType = dmnType;
        this.meterRegistry = registry;
    }

    @Override
    public void record(String decision, String endpointName, Boolean sample) {
        getCounter(decision, endpointName, sample.toString()).increment();
    }

    @Override
    public String getDmnType() {
        return dmnType;
    }

    private Counter getCounter(String decision, String endpoint, String identifier) {
        return Counter
                .builder(dmnType + DecisionConstants.DECISIONS_NAME_SUFFIX)
                .description(DecisionConstants.DECISIONS_HELP)
                .tags(Arrays.asList(Tag.of("decision", decision), Tag.of("endpoint", endpoint), Tag.of("identifier", identifier)))
                .register(meterRegistry);
    }
}
