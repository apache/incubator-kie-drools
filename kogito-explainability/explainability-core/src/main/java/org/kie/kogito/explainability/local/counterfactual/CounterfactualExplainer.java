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

import org.kie.kogito.explainability.local.LocalExplainer;
import org.kie.kogito.explainability.local.counterfactual.entities.CounterfactualEntity;
import org.kie.kogito.explainability.local.counterfactual.entities.CounterfactualEntityFactory;
import org.kie.kogito.explainability.model.*;
import org.optaplanner.core.api.solver.SolverJob;
import org.optaplanner.core.api.solver.SolverManager;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.config.solver.SolverManagerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Provides exemplar (counterfactual) explanations for a predictive model.
 * This implementation uses the Constraint Solution Problem solver OptaPlanner to search for
 * counterfactuals which minimize a score calculated by {@link CounterFactualScoreCalculator}.
 */
public class CounterfactualExplainer implements LocalExplainer<CounterfactualResult> {

    private static final Logger logger =
            LoggerFactory.getLogger(CounterfactualExplainer.class);
    private final List<Output> goal;
    private final DataDomain dataDomain;
    private final List<Boolean> constraints;
    private final SolverConfig solverConfig;
    private final Executor executor;
    private final DataDistribution dataDistribution;

    /**
     * Create a new {@link CounterfactualExplainer} using OptaPlanner as the underlying engine.
     * The data distribution information (if available) will be used to scale the features during the search.
     * The bounds of the search space must be specified using a {@link DataDomain} and any feature constraints
     * must be specified using a {@link List} of {@link Boolean}.
     * The desired outcome is passed using an {@link Output}, where the score of each feature represents the
     * minimum prediction score for a counterfactual to be considered.
     * A customizable OptaPlanner solver configuration can be passed using a {@link SolverConfig}.
     *
     * @param dataDistribution Characteristics of the data distribution as {@link DataDistribution}, if available
     * @param dataDomain A {@link DataDomain} which specifies the search space domain
     * @param contraints A list specifying by index which features are constrained
     * @param goal A collection of {@link Output} representing the desired outcome
     * @param solverConfig An OptaPlanner {@link SolverConfig} configuration
     */
    protected CounterfactualExplainer(DataDistribution dataDistribution,
            DataDomain dataDomain,
            List<Boolean> contraints,
            List<Output> goal,
            SolverConfig solverConfig,
            Executor executor) {
        this.dataDistribution = dataDistribution;
        this.dataDomain = dataDomain;
        this.constraints = contraints;
        this.goal = goal;
        this.solverConfig = solverConfig;
        this.executor = executor;
    }

    public static Builder builder(List<Output> goal, List<Boolean> constraints, DataDomain dataDomain) {
        return new Builder(goal, constraints, dataDomain);
    }

    private List<CounterfactualEntity> createEntities(PredictionInput predictionInput) {
        return IntStream.range(0, predictionInput.getFeatures().size())
                .mapToObj(featureIndex -> {
                    final Feature feature = predictionInput.getFeatures().get(featureIndex);
                    final Boolean isConstrained = constraints.get(featureIndex);
                    final FeatureDomain featureDomain = dataDomain.getFeatureDomains().get(featureIndex);
                    final FeatureDistribution featureDistribution = Optional
                            .ofNullable(dataDistribution)
                            .map(dd -> dd.asFeatureDistributions().get(featureIndex))
                            .orElse(null);
                    return CounterfactualEntityFactory
                            .from(feature, isConstrained, featureDomain, featureDistribution);
                }).collect(Collectors.toList());
    }

    @Override
    public CompletableFuture<CounterfactualResult> explainAsync(Prediction prediction, PredictionProvider model) {

        final List<CounterfactualEntity> entities = createEntities(prediction.getInput());

        final UUID problemId = UUID.randomUUID();

        final CompletableFuture<List<CounterfactualEntity>> cfEntities = CompletableFuture.supplyAsync(() -> {
            try (SolverManager<CounterfactualSolution, UUID> solverManager =
                    SolverManager.create(solverConfig, new SolverManagerConfig())) {

                CounterfactualSolution problem =
                        new CounterfactualSolution(entities, model, goal);

                SolverJob<CounterfactualSolution, UUID> solverJob = solverManager.solve(problemId, problem);
                CounterfactualSolution solution;
                try {
                    // Wait until the solving ends
                    solution = solverJob.getFinalBestSolution();
                    return solution.getEntities();
                } catch (ExecutionException e) {
                    logger.error("Solving failed: {}", e.getMessage());
                    throw new IllegalStateException("Prediction returned an error {}", e);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new IllegalStateException("Solving failed (Thread interrupted): {}", e);
                }
            }
        }, this.executor);

        final CompletableFuture<List<PredictionOutput>> cfOutputs =
                cfEntities.thenCompose(s -> model.predictAsync(List.of(new PredictionInput(
                        s.stream().map(CounterfactualEntity::asFeature).collect(Collectors.toList())))));

        return CompletableFuture.allOf(cfOutputs, cfEntities)
                .thenApply(v -> new CounterfactualResult(cfEntities.join(), cfOutputs.join()));
    }

    public static class Builder {

        private final DataDomain dataDomain;
        private final List<Boolean> constraints;
        private final List<Output> goal;
        private DataDistribution dataDistribution = null;
        private Executor executor = ForkJoinPool.commonPool();
        private SolverConfig solverConfig = null;

        private Builder(List<Output> goal, List<Boolean> constraints, DataDomain dataDomain) {
            this.goal = goal;
            this.constraints = constraints;
            this.dataDomain = dataDomain;
        }

        public Builder withDataDistribution(DataDistribution dataDistribution) {
            this.dataDistribution = dataDistribution;
            return this;
        }

        public Builder withExecutor(Executor executor) {
            this.executor = executor;
            return this;
        }

        public Builder withSolverConfig(SolverConfig solverConfig) {
            this.solverConfig = solverConfig;
            return this;
        }

        public CounterfactualExplainer build() {
            // Create a default solver configuration if none provided
            if (this.solverConfig == null) {
                this.solverConfig = CounterfactualConfigurationFactory.builder().build();
            }
            return new CounterfactualExplainer(dataDistribution,
                    dataDomain,
                    constraints,
                    goal,
                    solverConfig,
                    executor);
        }
    }
}
