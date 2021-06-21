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
package org.kie.kogito.monitoring.core.common.rule;

import java.util.Arrays;

import org.drools.core.event.rule.impl.AfterActivationFiredEventImpl;
import org.drools.core.event.rule.impl.BeforeActivationFiredEventImpl;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.BeforeMatchFiredEvent;
import org.kie.api.event.rule.DefaultAgendaEventListener;
import org.kie.kogito.KogitoGAV;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;

public class RuleMetricsListener extends DefaultAgendaEventListener {

    private static final Logger logger = LoggerFactory.getLogger(RuleMetricsListener.class);
    private final String identifier;
    private final KogitoGAV gav;
    private final MeterRegistry meterRegistry;

    private static final long NANOSECONDS_PER_MICROSECOND = 1_000_000;

    public RuleMetricsListener(String identifier, KogitoGAV gav, MeterRegistry meterRegistry) {
        this.identifier = identifier;
        this.gav = gav;
        this.meterRegistry = meterRegistry;
    }

    @Override
    public void beforeMatchFired(BeforeMatchFiredEvent event) {
        long nanoTime = System.nanoTime();
        BeforeActivationFiredEventImpl impl = getBeforeImpl(event);
        impl.setTimestamp(nanoTime);
    }

    @Override
    public void afterMatchFired(AfterMatchFiredEvent event) {
        AfterActivationFiredEventImpl afterImpl = getAfterImpl(event);
        BeforeActivationFiredEventImpl beforeImpl = getBeforeImpl(afterImpl.getBeforeMatchFiredEvent());
        long startTime = beforeImpl.getTimestamp();
        long elapsed = System.nanoTime() - startTime;
        String ruleName = event.getMatch().getRule().getName();

        getDroolsEvaluationTimeHistogram(identifier, ruleName).record(elapsed);
        if (logger.isDebugEnabled()) {
            logger.debug("Elapsed time: " + elapsed);
        }
    }

    public BeforeActivationFiredEventImpl getBeforeImpl(BeforeMatchFiredEvent e) {
        return (BeforeActivationFiredEventImpl) e;
    }

    public AfterActivationFiredEventImpl getAfterImpl(AfterMatchFiredEvent e) {
        return (AfterActivationFiredEventImpl) e;
    }

    private static long toMicro(long second) {
        return second * NANOSECONDS_PER_MICROSECOND;
    }

    private DistributionSummary getDroolsEvaluationTimeHistogram(String appId, String rule) {
        DistributionSummary distributionSummary = DistributionSummary.builder("drl_match_fired_nanosecond")
                .minimumExpectedValue((double) toMicro(1))
                .maximumExpectedValue((double) toMicro(10))
                .description("Drools Firing Time")
                .tags(Arrays.asList(Tag.of("app_id", appId), Tag.of("rule", rule), Tag.of("artifactId", gav.getArtifactId()), Tag.of("version", gav.getVersion())))
                .register(meterRegistry);
        return distributionSummary;
    }
}
