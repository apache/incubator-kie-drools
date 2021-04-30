/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.explainability.local.counterfactual;

import java.util.List;
import java.util.UUID;

import org.kie.kogito.explainability.local.counterfactual.entities.CounterfactualEntity;
import org.kie.kogito.explainability.model.Output;
import org.kie.kogito.explainability.model.PredictionProvider;
import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.score.buildin.bendablebigdecimal.BendableBigDecimalScore;

/**
 * Represents an OptaPlanner {@link PlanningSolution}.
 * This solution stores all the features as {@link CounterfactualEntity}, as well as a reference to the
 * {@link PredictionProvider} model.
 */
@PlanningSolution
public class CounterfactualSolution {

    @PlanningEntityCollectionProperty
    private List<CounterfactualEntity> entities;

    private List<Output> goal;

    private PredictionProvider model;

    private BendableBigDecimalScore score;

    private UUID solutionId;
    private UUID executionId;

    protected CounterfactualSolution() {
    }

    public CounterfactualSolution(
            List<CounterfactualEntity> entities,
            PredictionProvider model,
            List<Output> goal,
            UUID solutionId,
            UUID executionId) {
        this.entities = entities;
        this.model = model;
        this.goal = goal;
        this.solutionId = solutionId;
        this.executionId = executionId;
    }

    @PlanningScore(bendableHardLevelsSize = 3, bendableSoftLevelsSize = 1)
    public BendableBigDecimalScore getScore() {
        return score;
    }

    public void setScore(BendableBigDecimalScore score) {
        this.score = score;
    }

    public PredictionProvider getModel() {
        return model;
    }

    public List<Output> getGoal() {
        return goal;
    }

    public List<CounterfactualEntity> getEntities() {
        return entities;
    }

    public void setSolutionId(UUID solutionId) {
        this.solutionId = solutionId;
    }

    public UUID getSolutionId() {
        return solutionId;
    }

    public UUID getExecutionId() {
        return executionId;
    }

    public void setExecutionId(UUID executionId) {
        this.executionId = executionId;
    }
}
