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

import java.time.LocalDate;
import java.time.ZoneOffset;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Summary;

public class LocalDateHandler implements TypeHandlerWithSummary<LocalDate> {

    private final Summary summary;

    private String dmnType;

    public LocalDateHandler(String dmnType, CollectorRegistry registry) {
        this.dmnType = dmnType;
        this.summary = initializeDefaultSummary(dmnType, registry);
    }

    public LocalDateHandler(String dmnType) {
        this(dmnType, null);
    }

    @Override
    public void record(String type, String endpointName, LocalDate sample) {
        summary.labels(type, endpointName).observe(sample.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli());
    }

    @Override
    public String getDmnType() {
        return dmnType;
    }
}
