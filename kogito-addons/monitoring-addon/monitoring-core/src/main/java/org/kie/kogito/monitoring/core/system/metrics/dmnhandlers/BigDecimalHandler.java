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

import java.math.BigDecimal;

import io.micrometer.core.instrument.MeterRegistry;
import org.kie.kogito.monitoring.core.MonitoringRegistry;

public class BigDecimalHandler extends TypeHandlerWithSummary<BigDecimal> {

    private final String dmnType;

    public BigDecimalHandler(String dmnType) {
        this(dmnType, MonitoringRegistry.getDefaultMeterRegistry());
    }

    public BigDecimalHandler(String dmnType, MeterRegistry registry) {
        this.dmnType = dmnType;
        this.registry = registry;
    }

    @Override
    public void record(String decision, String endpointName, BigDecimal sample) {
        getDefaultSummary(dmnType, decision, endpointName).record(sample.doubleValue());
    }

    @Override
    public String getDmnType() {
        return dmnType;
    }
}
