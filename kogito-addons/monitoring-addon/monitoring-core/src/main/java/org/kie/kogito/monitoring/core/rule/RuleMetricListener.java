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

import org.drools.core.event.rule.impl.AfterActivationFiredEventImpl;
import org.drools.core.event.rule.impl.BeforeActivationFiredEventImpl;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.BeforeMatchFiredEvent;
import org.kie.api.event.rule.DefaultAgendaEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RuleMetricListener extends DefaultAgendaEventListener {

    private static final Logger logger = LoggerFactory.getLogger(RuleMetricListener.class);
    private final String identifier;

    public RuleMetricListener(String identifier) {
        this.identifier = identifier;
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

        RuleMetrics.getDroolsEvaluationTimeHistogram(identifier, ruleName).record(elapsed);
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
}
