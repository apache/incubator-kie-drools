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
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.kie.kogito.explainability.Config;
import org.kie.kogito.explainability.local.lime.LimeConfig;
import org.kie.kogito.explainability.local.lime.LimeExplainer;
import org.kie.kogito.explainability.model.FeatureImportance;
import org.kie.kogito.explainability.model.Prediction;
import org.kie.kogito.explainability.model.Saliency;
import org.kie.kogito.explainability.utils.ExplainabilityMetrics;
import org.optaplanner.core.api.score.buildin.simplebigdecimal.SimpleBigDecimalScore;
import org.optaplanner.core.api.score.calculator.EasyScoreCalculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LimeImpactScoreCalculator implements EasyScoreCalculator<LimeConfigSolution, SimpleBigDecimalScore> {

    private static final Logger LOGGER = LoggerFactory.getLogger(LimeImpactScoreCalculator.class);
    private static final int TOP_FEATURES = 2;

    @Override
    public SimpleBigDecimalScore calculateScore(LimeConfigSolution solution) {
        LimeConfig config = LimeConfigEntityFactory.toLimeConfig(solution);
        BigDecimal impactScore = BigDecimal.ZERO;
        List<Prediction> predictions = solution.getPredictions();
        if (!predictions.isEmpty()) {
            impactScore = getImpactScore(solution, config, predictions);
        }
        return SimpleBigDecimalScore.of(impactScore);
    }

    private BigDecimal getImpactScore(LimeConfigSolution solution, LimeConfig config, List<Prediction> predictions) {
        double succeededEvaluations = 0;
        BigDecimal impactScore = BigDecimal.ZERO;
        LimeExplainer limeExplainer = new LimeExplainer(config);
        for (Prediction prediction : predictions) {
            try {
                Map<String, Saliency> saliencyMap = limeExplainer.explainAsync(prediction, solution.getModel()).get(
                        Config.DEFAULT_ASYNC_TIMEOUT, Config.DEFAULT_ASYNC_TIMEUNIT);

                for (Map.Entry<String, Saliency> entry : saliencyMap.entrySet()) {
                    List<FeatureImportance> topFeatures = entry.getValue().getTopFeatures(TOP_FEATURES);
                    if (!topFeatures.isEmpty()) {
                        double v = ExplainabilityMetrics.impactScore(solution.getModel(),
                                prediction, topFeatures);
                        impactScore = impactScore.add(BigDecimal.valueOf(v));
                        succeededEvaluations++;
                    }
                }
            } catch (ExecutionException e) {
                LOGGER.error("Saliency impact-score calculation returned an error {}", e.getMessage());
            } catch (InterruptedException e) {
                LOGGER.error("Interrupted while waiting for saliency impact-score calculation {}", e.getMessage());
                Thread.currentThread().interrupt();
            } catch (TimeoutException e) {
                LOGGER.error("Timed out while waiting for saliency impact-score calculation", e);
            }
        }
        if (succeededEvaluations > 0) {
            impactScore = impactScore.divide(BigDecimal.valueOf(succeededEvaluations), RoundingMode.CEILING);
        }
        return impactScore;
    }

}
