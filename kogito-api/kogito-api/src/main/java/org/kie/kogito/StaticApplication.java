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

package org.kie.kogito;

import org.kie.kogito.decision.DecisionModels;
import org.kie.kogito.prediction.PredictionModels;
import org.kie.kogito.process.Processes;
import org.kie.kogito.rules.RuleUnits;
import org.kie.kogito.uow.UnitOfWorkManager;

public class StaticApplication implements Application {

    protected Config config;
    protected Processes processes;
    protected RuleUnits ruleUnits;
    protected DecisionModels decisionModels;
    protected PredictionModels predictionModels;

    public StaticApplication() {

    }

    public StaticApplication(
            Config config,
            Processes processes,
            RuleUnits ruleUnits,
            DecisionModels decisionModels,
            PredictionModels predictionModels) {
        this.config = config;
        this.processes = processes;
        this.ruleUnits = ruleUnits;
        this.decisionModels = decisionModels;
        this.predictionModels = predictionModels;

        if (config() != null && config().process() != null) {
            unitOfWorkManager().eventManager().setAddons(config().addons());
        }
    }

    public Config config() {
        return config;
    }

    @Override
    public Processes processes() {
        return processes;
    }

    @Override
    public RuleUnits ruleUnits() {
        return ruleUnits;
    }

    @Override
    public DecisionModels decisionModels() {
        return decisionModels;
    }

    @Override
    public PredictionModels predictionModels() {
        return predictionModels;
    }

    public UnitOfWorkManager unitOfWorkManager() {
        return config().process().unitOfWorkManager();
    }

}
