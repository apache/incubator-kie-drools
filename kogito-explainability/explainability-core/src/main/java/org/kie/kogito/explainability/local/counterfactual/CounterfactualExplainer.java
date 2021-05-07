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
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.kie.kogito.explainability.local.LocalExplainer;
import org.kie.kogito.explainability.local.counterfactual.entities.CounterfactualEntity;
import org.kie.kogito.explainability.local.counterfactual.entities.CounterfactualEntityFactory;
import org.kie.kogito.explainability.model.CounterfactualPrediction;
import org.kie.kogito.explainability.model.DataDistribution;
import org.kie.kogito.explainability.model.DataDomain;
import org.kie.kogito.explainability.model.Feature;
import org.kie.kogito.explainability.model.FeatureDistribution;
import org.kie.kogito.explainability.model.Output;
import org.kie.kogito.explainability.model.Prediction;
import org.kie.kogito.explainability.model.PredictionFeatureDomain;
import org.kie.kogito.explainability.model.PredictionInput;
import org.kie.kogito.explainability.model.PredictionOutput;
import org.kie.kogito.explainability.model.PredictionProvider;
import org.kie.kogito.explainability.model.domain.FeatureDomain;
import org.optaplanner.core.api.solver.SolverJob;
import org.optaplanner.core.api.solver.SolverManager;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.config.solver.SolverManagerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides exemplar (counterfactual) explanations for a predictive model.
 * This implementation uses the Constraint Solution Problem solver OptaPlanner to search for
 * counterfactuals which minimize a score calculated by {@link CounterFactualScoreCalculator}.
 */
public class CounterfactualExplainer implements LocalExplainer<CounterfactualResult> {

    private static final Logger logger =
            LoggerFactory.getLogger(CounterfactualExplainer.class);
    private final SolverConfig solverConfig;
    private final Executor executor;

    public static final Consumer<CounterfactualResult> defaultIntermediateConsumer =
            counterfactual -> logger.debug("Intermediate counterfactual: {}", counterfactual.getEntities());

    public static final Consumer<CounterfactualSolution> assignSolutionId =
            counterfactual -> counterfactual.setSolutionId(UUID.randomUUID());

    public CounterfactualExplainer() {
        this.solverConfig = CounterfactualConfigurationFactory.builder().build();
        this.executor = ForkJoinPool.commonPool();
    }

    /**
     * Create a new {@link CounterfactualExplainer} using OptaPlanner as the underlying engine.
     * The data distribution information (if available) will be used to scale the features during the search.
     * The bounds of the search space must be specified using a {@link DataDomain} and any feature constraints
     * must be specified using a {@link List} of {@link Boolean}.
     * The desired outcome is passed using an {@link Output}, where the score of each feature represents the
     * minimum prediction score for a counterfactual to be considered.
     * A customizable OptaPlanner solver configuration can be passed using a {@link SolverConfig}.
     * A {@link Consumer<CounterfactualSolution>} should be provided for the intermediate and final search results.
     *
     * @param solverConfig An OptaPlanner {@link SolverConfig} configuration
     */
    protected CounterfactualExplainer(SolverConfig solverConfig,
            Executor executor) {
        this.solverConfig = solverConfig;
        this.executor = executor;
    }

    public static Builder builder() {
        return new Builder();
    }

    private static List<CounterfactualEntity> createEntities(PredictionInput predictionInput,
            PredictionFeatureDomain featureDomain, List<Boolean> constraints, DataDistribution dataDistribution) {
        final List<FeatureDomain> domains = featureDomain.getFeatureDomains();
        return IntStream.range(0, predictionInput.getFeatures().size())
                .mapToObj(featureIndex -> {
                    final Feature feature = predictionInput.getFeatures().get(featureIndex);
                    final Boolean isConstrained = constraints.get(featureIndex);
                    final FeatureDomain domain = domains.get(featureIndex);
                    final FeatureDistribution featureDistribution = Optional
                            .ofNullable(dataDistribution)
                            .map(dd -> dd.asFeatureDistributions().get(featureIndex))
                            .orElse(null);
                    return CounterfactualEntityFactory
                            .from(feature, isConstrained, domain, featureDistribution);
                }).collect(Collectors.toList());
    }

    private Consumer<CounterfactualSolution> createSolutionConsumer(Consumer<CounterfactualResult> consumer) {
        return counterfactualSolution -> {
            CounterfactualResult result = new CounterfactualResult(counterfactualSolution.getEntities(),
                    counterfactualSolution.getPredictionOutputs(),
                    counterfactualSolution.getScore().isFeasible(),
                    counterfactualSolution.getSolutionId(), counterfactualSolution.getExecutionId());
            consumer.accept(result);
        };
    }

    @Override
    public CompletableFuture<CounterfactualResult> explainAsync(Prediction prediction, PredictionProvider model) {
        CounterfactualPrediction cfPrediction = (CounterfactualPrediction) prediction;
        final PredictionFeatureDomain featureDomain = cfPrediction.getDomain();
        final List<Boolean> constraints = cfPrediction.getConstraints();
        final UUID executionId = cfPrediction.getExecutionId();
        final List<CounterfactualEntity> entities =
                CounterfactualExplainer.createEntities(prediction.getInput(), featureDomain, constraints,
                        cfPrediction.getDataDistribution());

        final List<Output> goal = prediction.getOutput().getOutputs();

        Function<UUID, CounterfactualSolution> initial =
                uuid -> new CounterfactualSolution(entities, model, goal, UUID.randomUUID(), executionId);

        final CompletableFuture<CounterfactualSolution> cfSolution = CompletableFuture.supplyAsync(() -> {
            try (SolverManager<CounterfactualSolution, UUID> solverManager =
                    SolverManager.create(solverConfig, new SolverManagerConfig())) {

                final Consumer<CounterfactualResult> intermediateResultsConsumer =
                        cfPrediction.getIntermediateConsumer() == null ? defaultIntermediateConsumer
                                : cfPrediction.getIntermediateConsumer();

                SolverJob<CounterfactualSolution, UUID> solverJob =
                        solverManager.solveAndListen(executionId, initial,
                                assignSolutionId.andThen(createSolutionConsumer(intermediateResultsConsumer)),
                                null);
                try {
                    // Wait until the solving ends
                    return solverJob.getFinalBestSolution();
                } catch (ExecutionException e) {
                    logger.error("Solving failed: {}", e.getMessage());
                    throw new IllegalStateException("Prediction returned an error", e);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new IllegalStateException("Solving failed (Thread interrupted)", e);
                }
            }
        }, this.executor);

        final CompletableFuture<List<PredictionOutput>> cfOutputs =
                cfSolution.thenCompose(s -> model.predictAsync(List.of(new PredictionInput(
                        s.getEntities().stream().map(CounterfactualEntity::asFeature).collect(Collectors.toList())))));
        return CompletableFuture.allOf(cfOutputs, cfSolution).thenApply(v -> {
            CounterfactualSolution solution = cfSolution.join();
            return new CounterfactualResult(solution.getEntities(), cfOutputs.join(), solution.getScore().isFeasible(),
                    solution.getSolutionId(), solution.getExecutionId());
        });
    }

    public static class Builder {
        private Executor executor = ForkJoinPool.commonPool();
        private SolverConfig solverConfig = null;

        private Builder() {
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
            return new CounterfactualExplainer(
                    solverConfig,
                    executor);
        }
    }
}
