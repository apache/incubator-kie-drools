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
package org.kie.kogito.prediction.api;

import java.util.Optional;

import org.kie.kogito.internal.process.workitem.KogitoWorkItem;
import org.kie.kogito.internal.process.workitem.KogitoWorkItemHandler;
import org.kie.kogito.internal.process.workitem.KogitoWorkItemManager;
import org.kie.kogito.internal.process.workitem.WorkItemTransition;
import org.kie.kogito.process.workitems.impl.DefaultKogitoWorkItemHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PredictionAwareHumanTaskWorkItemHandler extends DefaultKogitoWorkItemHandler {

    private static final Logger logger = LoggerFactory.getLogger(PredictionAwareHumanTaskWorkItemHandler.class);

    private PredictionService predictionService;

    public PredictionAwareHumanTaskWorkItemHandler(PredictionService predictionService) {
        this.predictionService = predictionService;
    }

    public Optional<WorkItemTransition> activateWorkItemHandler(KogitoWorkItemManager manager, KogitoWorkItemHandler handler, KogitoWorkItem workItem, WorkItemTransition transition) {
        PredictionOutcome outcome = predictionService.predict(workItem, workItem.getParameters());
        logger.debug("Prediction service returned confidence level {} for work item {}", outcome.getConfidenceLevel(), workItem.getStringId());

        if (outcome.isCertain()) {
            workItem.setOutputs(outcome.getData());
            logger.debug("Prediction service is certain (confidence level {}) on the outputs, completing work item {}", outcome.getConfidenceLevel(), workItem.getStringId());

            return Optional.of(this.newTransition("skip", workItem.getPhaseStatus(), outcome.getData()));
        } else if (outcome.isPresent()) {
            logger.debug("Prediction service is NOT certain (confidence level {}) on the outputs, setting recommended outputs on work item {}",
                    outcome.getConfidenceLevel(),
                    workItem.getStringId());
            workItem.setOutputs(outcome.getData());
        }
        return Optional.empty();
    }

    public Optional<WorkItemTransition> completeWorkItemHandler(KogitoWorkItemManager manager, KogitoWorkItemHandler handler, KogitoWorkItem workItem, WorkItemTransition transition) {
        // upon actual transition train the data if it's completion phase
        predictionService.train(workItem, workItem.getParameters(), transition.data());
        return Optional.empty();
    }

}
