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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.kie.kogito.explainability.local.lime.LimeConfig;
import org.kie.kogito.explainability.model.Prediction;
import org.kie.kogito.explainability.model.PredictionProvider;
import org.optaplanner.core.api.score.buildin.simplebigdecimal.SimpleBigDecimalScore;
import org.optaplanner.core.api.score.calculator.EasyScoreCalculator;
import org.optaplanner.core.api.solver.SolverJob;
import org.optaplanner.core.api.solver.SolverManager;
import org.optaplanner.core.config.localsearch.LocalSearchPhaseConfig;
import org.optaplanner.core.config.localsearch.LocalSearchType;
import org.optaplanner.core.config.phase.PhaseConfig;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.config.solver.SolverManagerConfig;
import org.optaplanner.core.config.solver.termination.TerminationConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LimeConfigOptimizer {

    private static final Logger logger = LoggerFactory.getLogger(LimeConfigOptimizer.class);

    private static final long DEFAULT_TIME_LIMIT = 30;
    private static final boolean DEFAULT_PROXIMITY_ENTITIES = true;
    private static final boolean DEFAULT_SAMPLING_ENTITIES = true;
    private static final boolean DEFAULT_ENCODING_ENTITIES = true;
    private static final boolean DEFAULT_WEIGHTING_ENTITIES = true;

    private long timeLimit;
    private boolean proximityEntities;
    private boolean samplingEntities;
    private boolean encodingEntities;
    private boolean weightingEntities;
    private EasyScoreCalculator<LimeStabilitySolution, SimpleBigDecimalScore> scoreCalculator;

    public LimeConfigOptimizer() {
        this.timeLimit = DEFAULT_TIME_LIMIT;
        this.scoreCalculator = new LimeStabilityScoreCalculator();
        this.proximityEntities = DEFAULT_PROXIMITY_ENTITIES;
        this.samplingEntities = DEFAULT_SAMPLING_ENTITIES;
        this.encodingEntities = DEFAULT_ENCODING_ENTITIES;
        this.weightingEntities = DEFAULT_WEIGHTING_ENTITIES;
    }

    public LimeConfigOptimizer withTimeLimit(long timeLimit) {
        this.timeLimit = timeLimit;
        return this;
    }

    public LimeConfigOptimizer withProximity(boolean proximityEntities) {
        this.proximityEntities = proximityEntities;
        return this;
    }

    public LimeConfigOptimizer withSampling(boolean samplingEntities) {
        this.samplingEntities = samplingEntities;
        return this;
    }

    public LimeConfigOptimizer withEncoding(boolean encodingEntities) {
        this.encodingEntities = encodingEntities;
        return this;
    }

    public LimeConfigOptimizer withWeighting(boolean weightingEntities) {
        this.weightingEntities = weightingEntities;
        return this;
    }

    public LimeConfigOptimizer withScoreCalculator(EasyScoreCalculator<LimeStabilitySolution, SimpleBigDecimalScore> scoreCalculator) {
        this.scoreCalculator = scoreCalculator;
        return this;
    }

    public LimeConfig optimize(LimeConfig config, List<Prediction> predictions, PredictionProvider model) {
        List<LimeConfigEntity> entities = new ArrayList<>();
        if (samplingEntities) {
            entities.addAll(LimeConfigEntityFactory.createSamplingEntities(config));
        }
        if (proximityEntities) {
            entities.addAll(LimeConfigEntityFactory.createProximityEntities(config));
        }
        if (encodingEntities) {
            entities.addAll(LimeConfigEntityFactory.createEncodingEntities(config));
        }
        if (weightingEntities) {
            entities.addAll(LimeConfigEntityFactory.createWeightingEntities(config));
        }

        if (entities.isEmpty()) {
            return config;
        }

        LimeStabilitySolution initialSolution = new LimeStabilitySolution(config, predictions, entities, model);
        SolverConfig solverConfig = new SolverConfig();

        solverConfig.withEntityClasses(NumericLimeConfigEntity.class, BooleanLimeConfigEntity.class);

        solverConfig.withSolutionClass(LimeStabilitySolution.class);

        ScoreDirectorFactoryConfig scoreDirectorFactoryConfig = new ScoreDirectorFactoryConfig();
        scoreDirectorFactoryConfig.setEasyScoreCalculatorClass(scoreCalculator.getClass());
        solverConfig.setScoreDirectorFactoryConfig(scoreDirectorFactoryConfig);

        TerminationConfig terminationConfig = new TerminationConfig();
        terminationConfig.setSecondsSpentLimit(timeLimit);
        solverConfig.setTerminationConfig(terminationConfig);

        LocalSearchPhaseConfig localSearchPhaseConfig = new LocalSearchPhaseConfig();
        localSearchPhaseConfig.setLocalSearchType(LocalSearchType.LATE_ACCEPTANCE);

        @SuppressWarnings("rawtypes")
        List<PhaseConfig> phaseConfigs = new ArrayList<>();
        phaseConfigs.add(localSearchPhaseConfig);

        solverConfig.setPhaseConfigList(phaseConfigs);

        try (SolverManager<LimeStabilitySolution, UUID> solverManager =
                SolverManager.create(solverConfig, new SolverManagerConfig())) {

            UUID executionId = UUID.randomUUID();
            SolverJob<LimeStabilitySolution, UUID> solverJob =
                    solverManager.solve(executionId, initialSolution);
            try {
                // Wait until the solving ends
                LimeStabilitySolution finalBestSolution = solverJob.getFinalBestSolution();
                LimeConfig finalConfig = LimeConfigEntityFactory.toLimeConfig(finalBestSolution);
                BigDecimal score = finalBestSolution.getScore().getScore();
                logger.info("final best solution score {} with config {}", score, finalConfig);
                return finalConfig;
            } catch (ExecutionException e) {
                logger.error("Solving failed: {}", e.getMessage());
                throw new IllegalStateException("Prediction returned an error", e);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("Solving failed (Thread interrupted)", e);
            }
        }
    }
}
