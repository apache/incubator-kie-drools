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

import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;

public abstract class TypeHandlerWithSummary<T> implements TypeHandler<T> {

    protected MeterRegistry registry;

    protected DistributionSummary getDefaultSummary(String dmnType, String decision, String endpoint) {
        DistributionSummary summary = DistributionSummary
                .builder(dmnType.replace(" ", "_") + DecisionConstants.DECISIONS_NAME_SUFFIX)
                .description(DecisionConstants.DECISIONS_HELP)
                .publishPercentiles(DecisionConstants.SUMMARY_PERCENTILES)
                .distributionStatisticExpiry(DecisionConstants.SUMMARY_EXPIRATION)
                .tags(Arrays.asList(Tag.of("decision", decision), Tag.of("endpoint", endpoint)))
                .register(registry);
        return summary;
    }
}
