/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.monitoring.core.rule;

import java.util.Arrays;

import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.Tag;
import org.kie.kogito.monitoring.core.MonitoringRegistry;

public class RuleMetrics {

    private static final long NANOSECONDS_PER_MICROSECOND = 1_000_000;

    private static long toMicro(long second) {
        return second * NANOSECONDS_PER_MICROSECOND;
    }

    public static DistributionSummary getDroolsEvaluationTimeHistogram(String appId, String processId) {
        DistributionSummary distributionSummary = DistributionSummary.builder("drl_match_fired_nanosecond")
                .minimumExpectedValue((double) toMicro(1))
                .maximumExpectedValue((double) toMicro(10))
                .description("Drools Firing Time")
                .tags(Arrays.asList(Tag.of("app_id", appId), Tag.of("process_id", processId)))
                .register(MonitoringRegistry.getDefaultMeterRegistry());
        return distributionSummary;
    }
}
