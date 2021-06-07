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
package org.kie.kogito.monitoring.core.common.system.metrics;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.kie.dmn.api.core.DMNDecisionResult;
import org.kie.dmn.api.core.DMNResult;
import org.kie.kogito.KogitoGAV;
import org.kie.kogito.grafana.dmn.SupportedDecisionTypes;
import org.kie.kogito.monitoring.core.common.system.metrics.dmnhandlers.BigDecimalHandler;
import org.kie.kogito.monitoring.core.common.system.metrics.dmnhandlers.BooleanHandler;
import org.kie.kogito.monitoring.core.common.system.metrics.dmnhandlers.DaysAndTimeDurationHandler;
import org.kie.kogito.monitoring.core.common.system.metrics.dmnhandlers.LocalDateHandler;
import org.kie.kogito.monitoring.core.common.system.metrics.dmnhandlers.LocalDateTimeHandler;
import org.kie.kogito.monitoring.core.common.system.metrics.dmnhandlers.LocalTimeHandler;
import org.kie.kogito.monitoring.core.common.system.metrics.dmnhandlers.StringHandler;
import org.kie.kogito.monitoring.core.common.system.metrics.dmnhandlers.TypeHandler;
import org.kie.kogito.monitoring.core.common.system.metrics.dmnhandlers.YearsAndMonthsDurationHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.micrometer.core.instrument.MeterRegistry;

public class DMNResultMetricsBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(DMNResultMetricsBuilder.class);

    private final Map<Class, TypeHandler> handlers;

    public DMNResultMetricsBuilder(KogitoGAV gav, MeterRegistry meterRegistry) {
        handlers = generateHandlers(gav, meterRegistry);
    }

    private Map<Class, TypeHandler> generateHandlers(KogitoGAV gav, MeterRegistry meterRegistry) {
        HashMap<Class, TypeHandler> handlers = new HashMap<>();
        handlers.put(String.class, new StringHandler(SupportedDecisionTypes.fromInternalToStandard(String.class), gav, meterRegistry));
        handlers.put(Boolean.class, new BooleanHandler(SupportedDecisionTypes.fromInternalToStandard(Boolean.class), gav, meterRegistry));
        handlers.put(BigDecimal.class, new BigDecimalHandler(SupportedDecisionTypes.fromInternalToStandard(BigDecimal.class), gav, meterRegistry));
        handlers.put(LocalDateTime.class, new LocalDateTimeHandler(SupportedDecisionTypes.fromInternalToStandard(LocalDateTime.class), gav, meterRegistry));
        handlers.put(Duration.class, new DaysAndTimeDurationHandler(SupportedDecisionTypes.fromInternalToStandard(Duration.class), gav, meterRegistry));
        handlers.put(Period.class, new YearsAndMonthsDurationHandler(SupportedDecisionTypes.fromInternalToStandard(Period.class), gav, meterRegistry));
        handlers.put(LocalDate.class, new LocalDateHandler(SupportedDecisionTypes.fromInternalToStandard(LocalDate.class), gav, meterRegistry));
        handlers.put(LocalTime.class, new LocalTimeHandler(SupportedDecisionTypes.fromInternalToStandard(LocalTime.class), gav, meterRegistry));
        return handlers;
    }

    public Map<Class, TypeHandler> getHandlers() {
        return handlers;
    }

    public void generateMetrics(DMNResult dmnResult, String endpointName) {
        Optional<List<DMNDecisionResult>> optDecisionResults = Optional.ofNullable(dmnResult).map(DMNResult::getDecisionResults);

        if (optDecisionResults.isPresent()) {
            for (DMNDecisionResult decision : optDecisionResults.get()) {
                Object result = decision.getResult();
                if (result != null && SupportedDecisionTypes.isSupported(result.getClass())) {
                    handlers.get(result.getClass()).record(decision.getDecisionName(), endpointName, result);
                }
            }
        } else {
            LOGGER.warn("DMNResultMetricsBuilder can't register the metrics because the dmn result is null.");
        }
    }
}
