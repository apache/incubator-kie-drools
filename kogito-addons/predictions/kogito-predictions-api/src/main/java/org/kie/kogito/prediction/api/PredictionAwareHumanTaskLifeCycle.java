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
package org.kie.kogito.prediction.api;

import java.util.Map;
import java.util.Objects;

import org.jbpm.process.instance.impl.humantask.BaseHumanTaskLifeCycle;
import org.jbpm.process.instance.impl.humantask.HumanTaskWorkItemImpl;
import org.jbpm.process.instance.impl.workitem.Active;
import org.jbpm.process.instance.impl.workitem.Complete;
import org.kie.kogito.internal.process.runtime.KogitoWorkItem;
import org.kie.kogito.internal.process.runtime.KogitoWorkItemManager;
import org.kie.kogito.process.workitem.InvalidLifeCyclePhaseException;
import org.kie.kogito.process.workitem.LifeCyclePhase;
import org.kie.kogito.process.workitem.Transition;
import org.kie.kogito.process.workitems.InternalKogitoWorkItemManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PredictionAwareHumanTaskLifeCycle extends BaseHumanTaskLifeCycle {

    private static final Logger logger = LoggerFactory.getLogger(PredictionAwareHumanTaskLifeCycle.class);

    private PredictionService predictionService;

    public PredictionAwareHumanTaskLifeCycle(PredictionService predictionService) {
        this.predictionService = Objects.requireNonNull(predictionService);
    }

    @Override
    public Map<String, Object> transitionTo(KogitoWorkItem workItem, KogitoWorkItemManager manager, Transition<Map<String, Object>> transition) {
        LifeCyclePhase targetPhase = phaseById(transition.phase());
        if (targetPhase == null) {
            logger.debug("Target life cycle phase '{}' does not exist in {}", transition.phase(), this.getClass().getSimpleName());
            throw new InvalidLifeCyclePhaseException(transition.phase());
        }

        HumanTaskWorkItemImpl humanTaskWorkItem = (HumanTaskWorkItemImpl) workItem;
        if (targetPhase.id().equals(Active.ID)) {

            PredictionOutcome outcome = predictionService.predict(workItem, workItem.getParameters());
            logger.debug("Prediction service returned confidence level {} for work item {}", outcome.getConfidenceLevel(), humanTaskWorkItem.getStringId());

            if (outcome.isCertain()) {
                humanTaskWorkItem.getResults().putAll(outcome.getData());
                logger.debug("Prediction service is certain (confidence level {}) on the outputs, completing work item {}", outcome.getConfidenceLevel(), humanTaskWorkItem.getStringId());
                ((InternalKogitoWorkItemManager) manager).internalCompleteWorkItem(humanTaskWorkItem);

                return outcome.getData();
            } else if (outcome.isPresent()) {
                logger.debug("Prediction service is NOT certain (confidence level {}) on the outputs, setting recommended outputs on work item {}", outcome.getConfidenceLevel(),
                        humanTaskWorkItem.getStringId());
                humanTaskWorkItem.getResults().putAll(outcome.getData());

            }
        }

        // prediction service does work only on activating tasks
        Map<String, Object> data = super.transitionTo(workItem, manager, transition);
        if (targetPhase.id().equals(Complete.ID)) {
            // upon actual transition train the data if it's completion phase
            predictionService.train(humanTaskWorkItem, workItem.getParameters(), data);
        }
        return data;
    }

}
