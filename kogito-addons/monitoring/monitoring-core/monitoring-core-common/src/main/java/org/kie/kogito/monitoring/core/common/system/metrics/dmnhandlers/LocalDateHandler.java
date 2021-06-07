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
package org.kie.kogito.monitoring.core.common.system.metrics.dmnhandlers;

import java.time.LocalDate;
import java.time.ZoneOffset;

import org.kie.kogito.KogitoGAV;

import io.micrometer.core.instrument.MeterRegistry;

public class LocalDateHandler extends TypeHandlerWithSummary<LocalDate> {

    private final String dmnType;

    public LocalDateHandler(String dmnType, KogitoGAV gav, MeterRegistry registry) {
        this.dmnType = dmnType;
        setRegistry(registry);
        setKogitoGAV(gav);
    }

    @Override
    public void record(String type, String endpointName, LocalDate sample) {
        getDefaultSummary(dmnType, type, endpointName).record(sample.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli());
    }

    @Override
    public String getDmnType() {
        return dmnType;
    }
}
