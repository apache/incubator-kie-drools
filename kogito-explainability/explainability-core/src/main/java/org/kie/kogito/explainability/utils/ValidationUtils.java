/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.explainability.utils;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.kie.kogito.explainability.local.LocalExplainer;
import org.kie.kogito.explainability.model.Prediction;
import org.kie.kogito.explainability.model.PredictionProvider;
import org.kie.kogito.explainability.model.Saliency;

/**
 * Utility class for validating desiderata about models' explainability.
 */
public class ValidationUtils {

    /**
     * Validate local saliency stability scores.
     * 
     * @param model model to validate
     * @param prediction the prediction to be used to evaluate stability
     * @param explainer the local saliency explainer
     * @param topK the no. of features to account for
     * @param minimumPositiveStabilityScore minimum positive stability score
     * @param minimumNegativeStabilityScore minimum negative stability score
     * @throws ValidationException if either positive or negative stability scores are lower than minimum for any decision
     */
    public static void validateLocalSaliencyStability(PredictionProvider model, Prediction prediction,
            LocalExplainer<Map<String, Saliency>> explainer,
            int topK, double minimumPositiveStabilityScore,
            double minimumNegativeStabilityScore)
            throws ValidationException, InterruptedException, ExecutionException, TimeoutException {
        LocalSaliencyStability stability = ExplainabilityMetrics.getLocalSaliencyStability(model, prediction, explainer,
                topK, 10);
        for (int i = 1; i <= topK; i++) {
            for (String decision : stability.getDecisions()) {
                double positiveStabilityScore = stability.getPositiveStabilityScore(decision, i);
                double negativeStabilityScore = stability.getNegativeStabilityScore(decision, i);
                if (positiveStabilityScore < minimumPositiveStabilityScore) {
                    throw new ValidationException("Expected positive stability score bigger than "
                            + minimumPositiveStabilityScore + ". Got:" + positiveStabilityScore
                            + " for " + decision + "@k=" + i);
                }
                if (negativeStabilityScore < minimumNegativeStabilityScore) {
                    throw new ValidationException("Expected negative stability score bigger than "
                            + minimumNegativeStabilityScore + ". Got:" + negativeStabilityScore
                            + " for " + decision + "@k=" + i);
                }
            }
        }
    }

    /**
     * Checked exception for validation purposes.
     */
    public static class ValidationException extends Exception {
        public ValidationException(String message) {
            super(message);
        }
    }
}
