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
package org.kie.kogito.explainability.local.lime.optim;

import java.util.ArrayList;
import java.util.List;

import org.kie.kogito.explainability.local.lime.LimeConfig;
import org.kie.kogito.explainability.model.Prediction;
import org.kie.kogito.explainability.model.PredictionProvider;
import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.score.buildin.simplebigdecimal.SimpleBigDecimalScore;

@PlanningSolution
public class LimeConfigSolution {

    private LimeConfig config = new LimeConfig();

    private List<Prediction> predictions;

    @PlanningEntityCollectionProperty
    private List<LimeConfigEntity> entities = new ArrayList<>();

    private PredictionProvider model;

    @PlanningScore
    private SimpleBigDecimalScore score;

    public LimeConfigSolution() {
    }

    public LimeConfigSolution(LimeConfig config, List<Prediction> predictions,
            List<LimeConfigEntity> entities, PredictionProvider model) {
        this.config = config;
        this.predictions = predictions;
        this.entities = entities;
        this.model = model;
    }

    public LimeConfig getConfig() {
        return config;
    }

    public void setConfig(LimeConfig config) {
        this.config = config;
    }

    public PredictionProvider getModel() {
        return model;
    }

    public List<Prediction> getPredictions() {
        return predictions;
    }

    public SimpleBigDecimalScore getScore() {
        return score;
    }

    public void setScore(SimpleBigDecimalScore score) {
        this.score = score;
    }

    public List<LimeConfigEntity> getEntities() {
        return entities;
    }

    @Override
    public String toString() {
        return "LimeStabilitySolution{" +
                "config=" + config +
                '}';
    }
}
