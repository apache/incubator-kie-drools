/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.kogito.jitexecutor.dmn;

import java.util.ArrayList;
import java.util.List;

import org.kie.dmn.api.core.event.AfterConditionalEvaluationEvent;
import org.kie.dmn.api.core.event.AfterEvaluateAllEvent;
import org.kie.dmn.api.core.event.AfterEvaluateBKMEvent;
import org.kie.dmn.api.core.event.AfterEvaluateContextEntryEvent;
import org.kie.dmn.api.core.event.AfterEvaluateDecisionEvent;
import org.kie.dmn.api.core.event.AfterEvaluateDecisionServiceEvent;
import org.kie.dmn.api.core.event.AfterEvaluateDecisionTableEvent;
import org.kie.dmn.api.core.event.AfterInvokeBKMEvent;
import org.kie.dmn.api.core.event.DMNEvent;
import org.kie.dmn.api.core.event.DMNRuntimeEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JITDMNListener implements DMNRuntimeEventListener {

    private final List<String> evaluationHitIds = new ArrayList<>();

    private static final Logger LOGGER = LoggerFactory.getLogger(JITDMNListener.class);

    @Override
    public void afterEvaluateDecisionTable(AfterEvaluateDecisionTableEvent event) {
        logEvent(event);
        evaluationHitIds.addAll(event.getSelectedIds());
    }

    @Override
    public void afterEvaluateDecision(AfterEvaluateDecisionEvent event) {
        logEvent(event);
    }

    @Override
    public void afterEvaluateBKM(AfterEvaluateBKMEvent event) {
        logEvent(event);
    }

    @Override
    public void afterEvaluateContextEntry(AfterEvaluateContextEntryEvent event) {
        logEvent(event);
    }

    @Override
    public void afterEvaluateDecisionService(AfterEvaluateDecisionServiceEvent event) {
        logEvent(event);
    }

    @Override
    public void afterInvokeBKM(AfterInvokeBKMEvent event) {
        logEvent(event);
    }

    @Override
    public void afterEvaluateAll(AfterEvaluateAllEvent event) {
        logEvent(event);
    }

    @Override
    public void afterConditionalEvaluation(AfterConditionalEvaluationEvent event) {
        logEvent(event);
        evaluationHitIds.add(event.getExecutedId());
    }

    public List<String> getEvaluationHitIds() {
        return evaluationHitIds;
    }

    private void logEvent(DMNEvent toLog) {
        LOGGER.info("{} event {}", toLog.getClass().getSimpleName(), toLog);
    }

    private void logEvent(AfterConditionalEvaluationEvent toLog) {
        LOGGER.info("{} event {}", toLog.getClass().getSimpleName(), toLog);
    }

}
