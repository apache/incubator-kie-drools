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
import java.time.Duration;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.Set;
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
import org.kie.kogito.explainability.model.Type;
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

    public static Double outputDistance(Output prediction, Output goal) throws IllegalArgumentException {
        return outputDistance(prediction, goal, 0.0);
    }

    private static final Set<Type> SUPPORTED_CATEGORICAL_TYPES = Set.of(
            Type.CATEGORICAL,
            Type.BOOLEAN,
            Type.TEXT,
            Type.CURRENCY,
            Type.BINARY,
            Type.UNDEFINED);

    public static Double outputDistance(Output prediction, Output goal, double threshold) throws IllegalArgumentException {
        final Type predictionType = prediction.getType();
        final Type goalType = goal.getType();

        if (predictionType != goalType) {
            String message = String.format("Features must have the same type. Feature '%s', has type '%s' and '%s'",
                    prediction.getName(), predictionType.toString(), goalType.toString());
            logger.error(message);
            throw new IllegalArgumentException(message);
        }

        if (predictionType == Type.NUMBER) {
            final double predictionValue = prediction.getValue().asNumber();
            final double goalValue = goal.getValue().asNumber();
            final double difference = Math.abs(predictionValue - goalValue);
            // If any of the values is zero use the difference instead of change
            // If neither of the values is zero use the change rate
            double distance;
            if (Double.isNaN(predictionValue) || Double.isNaN(goalValue)) {
                String message = String.format("Unsupported NaN or NULL for numeric feature '%s'", prediction.getName());
                logger.error(message);
                throw new IllegalArgumentException(message);
            }
            if (predictionValue == 0 || goalValue == 0) {
                distance = difference;
            } else {
                distance = difference / Math.max(predictionValue, goalValue);
            }
            if (distance < threshold) {
                return 0d;
            } else {
                return distance;
            }
        } else if (predictionType == Type.DURATION) {
            final Duration predictionValue = (Duration) prediction.getValue().getUnderlyingObject();
            final Duration goalValue = (Duration) goal.getValue().getUnderlyingObject();

            if (Objects.isNull(predictionValue) || Objects.isNull(goalValue)) {
                return 1.0;
            }
            // Duration distances calculated from value in seconds
            final double difference = predictionValue.minus(goalValue).abs().getSeconds();
            // If any of the values is zero use the difference instead of change
            // If neither of the values is zero use the change rate
            double distance;
            if (predictionValue.isZero() || goalValue.isZero()) {
                distance = difference;
            } else {
                distance = difference / Math.max(predictionValue.getSeconds(), goalValue.getSeconds());
            }
            if (distance < threshold) {
                return 0d;
            } else {
                return distance;
            }
        } else if (predictionType == Type.TIME) {
            final LocalTime predictionValue = (LocalTime) prediction.getValue().getUnderlyingObject();
            final LocalTime goalValue = (LocalTime) goal.getValue().getUnderlyingObject();

            if (Objects.isNull(predictionValue) || Objects.isNull(goalValue)) {
                return 1.0;
            }
            final double interval = LocalTime.MIN.until(LocalTime.MAX, ChronoUnit.SECONDS);
            // Time distances calculated from value in seconds
            final double distance = Math.abs(predictionValue.until(goalValue, ChronoUnit.SECONDS)) / interval;
            if (distance < threshold) {
                return 0d;
            } else {
                return distance;
            }
        } else if (SUPPORTED_CATEGORICAL_TYPES.contains(predictionType)) {
            final Object goalValueObject = goal.getValue().getUnderlyingObject();
            final Object predictionValueObject = prediction.getValue().getUnderlyingObject();
            return Objects.equals(goalValueObject, predictionValueObject) ? 0.0 : 1.0;
        } else {
            String message =
                    String.format("Feature '%s' has unsupported type '%s'", prediction.getName(), predictionType.toString());
            logger.error(message);
            throw new IllegalArgumentException(message);
        }
    }

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
        int secondarySoftscore = 0;

        StringBuilder builder = new StringBuilder();

        // Calculate similarities between original inputs and proposed inputs
        double inputSimilarities = 0.0;
        final int numberOfEntities = solution.getEntities().size();
        for (CounterfactualEntity entity : solution.getEntities()) {
            final double entitySimilarity = entity.similarity();
            inputSimilarities += entitySimilarity / (double) numberOfEntities;
            final Feature f = entity.asFeature();
            builder.append(String.format("%s=%s (d:%f)", f.getName(), f.getValue().getUnderlyingObject(), entitySimilarity));

            if (entity.isChanged()) {
                secondarySoftscore -= 1;

                if (entity.isConstrained()) {
                    secondaryHardScore -= 1;
                }
            }
        }
        // Calculate Gower distance from the similarities
        final double primarySoftScore = -Math.sqrt(Math.abs(1.0 - inputSimilarities));

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

            double outputDistance = 0.0;

            for (PredictionOutput predictionOutput : predictions) {

                final List<Output> outputs = predictionOutput.getOutputs();

                if (goal.size() != outputs.size()) {
                    throw new IllegalArgumentException("Prediction size must be equal to goal size");
                }

                final int numberOutputs = outputs.size();
                for (int i = 0; i < numberOutputs; i++) {
                    final Output output = outputs.get(i);
                    final Output goalOutput = goal.get(i);
                    final double d =
                            CounterFactualScoreCalculator.outputDistance(output, goalOutput, solution.getGoalThreshold());
                    outputDistance += d * d;

                    if (output.getScore() < goalOutput.getScore()) {
                        tertiaryHardScore -= 1;
                    }
                }
                primaryHardScore -= Math.sqrt(outputDistance);
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
