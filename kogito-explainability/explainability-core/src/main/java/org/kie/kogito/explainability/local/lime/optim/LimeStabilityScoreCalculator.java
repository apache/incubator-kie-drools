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
import java.math.RoundingMode;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.kie.kogito.explainability.local.lime.LimeConfig;
import org.kie.kogito.explainability.local.lime.LimeExplainer;
import org.kie.kogito.explainability.model.Prediction;
import org.kie.kogito.explainability.model.PredictionProvider;
import org.kie.kogito.explainability.utils.ExplainabilityMetrics;
import org.kie.kogito.explainability.utils.LocalSaliencyStability;
import org.optaplanner.core.api.score.buildin.simplebigdecimal.SimpleBigDecimalScore;
import org.optaplanner.core.api.score.calculator.EasyScoreCalculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LimeStabilityScoreCalculator implements EasyScoreCalculator<LimeConfigSolution, SimpleBigDecimalScore> {

    private static final Logger LOGGER = LoggerFactory.getLogger(LimeStabilityScoreCalculator.class);
    private static final BigDecimal TWO = BigDecimal.valueOf(2d);
    private static final BigDecimal ZERO = BigDecimal.valueOf(0);
    private static final int NUM_RUNS = 5;

    private final BigDecimal negativeWeight;
    private final BigDecimal positiveWeight;

    public LimeStabilityScoreCalculator(BigDecimal negativeWeight, BigDecimal positiveWeight) {
        this.negativeWeight = negativeWeight;
        this.positiveWeight = positiveWeight;
    }

    public LimeStabilityScoreCalculator() {
        this.negativeWeight = BigDecimal.valueOf(0.5);
        this.positiveWeight = BigDecimal.valueOf(0.5);
    }

    @Override
    public SimpleBigDecimalScore calculateScore(LimeConfigSolution solution) {
        LimeConfig config = LimeConfigEntityFactory.toLimeConfig(solution);
        BigDecimal stabilityScore = BigDecimal.ZERO;
        List<Prediction> predictions = solution.getPredictions();
        if (!predictions.isEmpty()) {
            stabilityScore = getStabilityScore(solution.getModel(), config, predictions);
        }
        return SimpleBigDecimalScore.of(stabilityScore);
    }

    private BigDecimal getStabilityScore(PredictionProvider model, LimeConfig config, List<Prediction> predictions) {
        double succeededEvaluations = 0;
        BigDecimal stabilityScore = BigDecimal.ZERO;
        LimeExplainer limeExplainer = new LimeExplainer(config);
        for (Prediction prediction : predictions) {
            try {
                LocalSaliencyStability stability = ExplainabilityMetrics.getLocalSaliencyStability(model, prediction,
                        limeExplainer, TWO.intValue(), NUM_RUNS);
                for (String decision : stability.getDecisions()) {
                    BigDecimal decisionMarginalScore = getDecisionMarginalScore(stability, decision);
                    stabilityScore = stabilityScore.add(decisionMarginalScore);
                    succeededEvaluations++;
                }
            } catch (ExecutionException e) {
                LOGGER.error("Saliency stability calculation returned an error {}", e.getMessage());
            } catch (InterruptedException e) {
                LOGGER.error("Interrupted while waiting for saliency stability calculation {}", e.getMessage());
                Thread.currentThread().interrupt();
            } catch (TimeoutException e) {
                LOGGER.error("Timed out while waiting for saliency stability calculation", e);
            }
        }
        if (succeededEvaluations > 0) {
            stabilityScore = stabilityScore.divide(BigDecimal.valueOf(succeededEvaluations), RoundingMode.CEILING);
        }
        return stabilityScore;
    }

    private BigDecimal getDecisionMarginalScore(LocalSaliencyStability stability, String decision) {
        BigDecimal positiveStabilityScore = ZERO;
        BigDecimal negativeStabilityScore = ZERO;
        for (int i = 1; i <= LimeStabilityScoreCalculator.TWO.intValue(); i++) {
            positiveStabilityScore = positiveStabilityScore.add(BigDecimal.valueOf(stability.getPositiveStabilityScore(decision, i)));
            negativeStabilityScore = negativeStabilityScore.add(BigDecimal.valueOf(stability.getNegativeStabilityScore(decision, i)));
        }
        positiveStabilityScore = positiveStabilityScore.divide(LimeStabilityScoreCalculator.TWO, RoundingMode.CEILING);
        negativeStabilityScore = negativeStabilityScore.divide(LimeStabilityScoreCalculator.TWO, RoundingMode.CEILING);
        return (positiveStabilityScore.multiply(positiveWeight)).add(negativeStabilityScore.multiply(negativeWeight))
                .divide(BigDecimal.valueOf(stability.getDecisions().size()), RoundingMode.CEILING);
    }

}
