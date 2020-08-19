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

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.kie.kogito.explainability.model.Feature;
import org.kie.kogito.explainability.model.FeatureImportance;
import org.kie.kogito.explainability.model.Output;
import org.kie.kogito.explainability.model.Prediction;
import org.kie.kogito.explainability.model.PredictionInput;
import org.kie.kogito.explainability.model.PredictionOutput;
import org.kie.kogito.explainability.model.PredictionProvider;
import org.kie.kogito.explainability.model.Saliency;
import org.kie.kogito.explainability.model.Type;

/**
 * Utility class providing different methods to evaluate explainability.
 */
public class ExplainabilityMetrics {

    /**
     * Drop in confidence score threshold for impact score calculation.
     * Confidence scores below {@code originalScore * CONFIDENCE_DROP_RATIO} are considered impactful for a model.
     */
    private static final double CONFIDENCE_DROP_RATIO = 0.2d;

    private ExplainabilityMetrics() {
    }

    /**
     * Measure the explainability of an explanation.
     * See paper: "Towards Quantification of Explainability in Explainable Artificial Intelligence Methods" by Islam et al.
     *
     * @param inputCognitiveChunks  the no. of cognitive chunks (pieces of information) required to generate the
     *                              explanation (e.g. the no. of explanation inputs)
     * @param outputCognitiveChunks the no. of cognitive chunks generated within the explanation itself
     * @param interactionRatio      the ratio of interaction (between 0 and 1) required by the explanation
     * @return the quantitative explainability measure
     */
    public static double quantifyExplainability(int inputCognitiveChunks, int outputCognitiveChunks, double interactionRatio) {
        return inputCognitiveChunks + outputCognitiveChunks > 0 ? 0.333 / (double) inputCognitiveChunks
                + 0.333 / (double) outputCognitiveChunks + 0.333 * (1d - interactionRatio) : 0;
    }

    /**
     * Calculate the impact of dropping the most important features (given by {@link Saliency#getTopFeatures(int)} from the input.
     * Highly important features would have rather high impact.
     * See paper: Qiu Lin, Zhong, et al. "Do Explanations Reflect Decisions? A Machine-centric Strategy to Quantify the
     * Performance of Explainability Algorithms." 2019.
     *
     * @param model       the model to be explained
     * @param prediction  a prediction
     * @param topFeatures the list of important features that should be dropped
     * @return the saliency impact
     */
    public static double impactScore(PredictionProvider model, Prediction prediction, List<FeatureImportance> topFeatures) {
        List<Feature> copy = List.copyOf(prediction.getInput().getFeatures());
        for (FeatureImportance featureImportance : topFeatures) {
            copy = DataUtils.dropFeature(copy, featureImportance.getFeature());
        }

        PredictionInput predictionInput = new PredictionInput(copy);
        List<PredictionOutput> predictionOutputs = model.predict(List.of(predictionInput));
        PredictionOutput predictionOutput = predictionOutputs.get(0);
        double impact = 0d;
        double size = predictionOutput.getOutputs().size();
        for (int i = 0; i < size; i++) {
            Output original = prediction.getOutput().getOutputs().get(i);
            Output modified = predictionOutput.getOutputs().get(i);
            impact += (!original.getValue().asString().equals(modified.getValue().asString())
                    || modified.getScore() < original.getScore() * CONFIDENCE_DROP_RATIO) ? 1d : 0d;
        }
        return impact / size;
    }

    /**
     * Calculate fidelity (accuracy) of boolean classification outputs using saliency predictor function = sign(sum(saliency.scores))
     * See papers:
     * - Guidotti Riccardo, et al. "A survey of methods for explaining black box models." ACM computing surveys (2018).
     * - Bodria, Francesco, et al. "Explainability Methods for Natural Language Processing: Applications to Sentiment Analysis (Discussion Paper)."
     *
     * @param pairs pairs composed by the saliency and the related prediction
     * @return the fidelity accuracy
     */
    public static double classificationFidelity(List<Pair<Saliency, Prediction>> pairs) {
        double acc = 0;
        double evals = 0;
        for (Pair<Saliency, Prediction> pair : pairs) {
            Saliency saliency = pair.getLeft();
            Prediction prediction = pair.getRight();
            for (Output output : prediction.getOutput().getOutputs()) {
                Type type = output.getType();
                if (Type.BOOLEAN.equals(type)) {
                    double predictorOutput = saliency.getPerFeatureImportance().stream().map(FeatureImportance::getScore).mapToDouble(d -> d).sum();
                    double v = output.getValue().asNumber();
                    if ((v >= 0 && predictorOutput >= 0) || (v < 0 && predictorOutput < 0)) {
                        acc++;
                    }
                    evals++;
                }
            }
        }
        return evals == 0 ? 0 : acc / evals;
    }
}
