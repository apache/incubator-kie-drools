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

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import org.kie.kogito.explainability.Config;
import org.kie.kogito.explainability.local.counterfactual.entities.CounterfactualEntity;
import org.kie.kogito.explainability.model.Feature;
import org.kie.kogito.explainability.model.Output;
import org.kie.kogito.explainability.model.PredictionInput;
import org.kie.kogito.explainability.model.PredictionOutput;
import org.optaplanner.core.api.score.buildin.bendablebigdecimal.BendableBigDecimalScore;
import org.optaplanner.core.api.score.calculator.EasyScoreCalculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Counterfactual score calculator.
 * The score is implementabled as a {@link BendableBigDecimalScore} with two hard levels and one soft level.
 * The primary hard level penalizes solutions which do not meet the required outcome.
 * The second hard level penalizes solutions which change constrained {@link CounterfactualEntity}.
 * The soft level penalizes solutions according to their distance from the original prediction inputs.
 */
public class CounterFactualScoreCalculator implements EasyScoreCalculator<CounterfactualSolution, BendableBigDecimalScore> {

    private static final Logger logger =
            LoggerFactory.getLogger(CounterFactualScoreCalculator.class);

    /**
     * Calculates the counterfactual score for each proposed solution.
     * This method assumes that each model used as {@link org.kie.kogito.explainability.model.PredictionProvider} is
     * consistent, in the sense that for repeated operations, the size of the returned collection of
     * {@link PredictionOutput} is the same, if the size of {@link PredictionInput} doesn't change.
     *
     * @param solution Proposed solution
     * @return A {@link BendableBigDecimalScore} with three "hard" levels and one "soft" level
     */
    @Override
    public BendableBigDecimalScore calculateScore(CounterfactualSolution solution) {

        double primaryHardScore = 0;
        int secondaryHardScore = 0;
        int tertiaryHardScore = 0;
        double primarySoftScore = 0.0;
        int secondarySoftscore = 0;

        StringBuilder builder = new StringBuilder();

        for (CounterfactualEntity entity : solution.getEntities()) {
            final double entityDistance = entity.distance();
            primarySoftScore += entityDistance;
            final Feature f = entity.asFeature();
            builder.append(String.format("%s=%s (d:%f)", f.getName(), f.getValue().getUnderlyingObject(), entityDistance));

            if (entity.isChanged()) {
                secondarySoftscore -= 1;

                if (entity.isConstrained()) {
                    secondaryHardScore -= 1;
                }
            }
        }

        logger.debug("Current solution: {}", builder);

        List<Feature> input = solution.getEntities().stream().map(CounterfactualEntity::asFeature).collect(Collectors.toList());

        PredictionInput predictionInput = new PredictionInput(input);

        List<PredictionInput> inputs = List.of(predictionInput);

        CompletableFuture<List<PredictionOutput>> predictionAsync = solution.getModel().predictAsync(inputs);

        final List<Output> goal = solution.getGoal();

        try {
            List<PredictionOutput> predictions = predictionAsync.get(Config.INSTANCE.getAsyncTimeout(),
                    Config.INSTANCE.getAsyncTimeUnit());

            solution.setPredictionOutputs(predictions);

            double distance = 0.0;

            for (PredictionOutput predictionOutput : predictions) {

                final List<Output> outputs = predictionOutput.getOutputs();

                if (goal.size() != outputs.size()) {
                    throw new IllegalArgumentException("Prediction size must be equal to goal size");
                }
                for (int i = 0; i < outputs.size(); i++) {
                    final Output output = outputs.get(i);
                    final Output goalOutput = goal.get(i);
                    final double d = goalOutput.getValue().asNumber() - output.getValue().asNumber();
                    distance += d * d;
                    if (output.getScore() < goalOutput.getScore()) {
                        tertiaryHardScore -= 1;
                    }
                }
                primaryHardScore -= Math.sqrt(distance);
                logger.debug("Distance penalty: {}", primaryHardScore);
                logger.debug("Changed constraints penalty: {}", secondaryHardScore);
                logger.debug("Confidence threshold penalty: {}", tertiaryHardScore);
            }

        } catch (ExecutionException e) {
            logger.error("Prediction returned an error {}", e.getMessage());
        } catch (InterruptedException e) {
            logger.error("Interrupted while waiting for prediction {}", e.getMessage());
            Thread.currentThread().interrupt();
        } catch (TimeoutException e) {
            logger.error("Timed out while waiting for prediction");
        }

        logger.debug("Feature distance: {}", -Math.abs(primarySoftScore));
        return BendableBigDecimalScore.of(
                new BigDecimal[] {
                        BigDecimal.valueOf(primaryHardScore),
                        BigDecimal.valueOf(secondaryHardScore),
                        BigDecimal.valueOf(tertiaryHardScore)
                },
                new BigDecimal[] { BigDecimal.valueOf(-Math.abs(primarySoftScore)), BigDecimal.valueOf(secondarySoftscore) });
    }
}
