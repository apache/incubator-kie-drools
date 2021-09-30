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
package org.kie.kogito.explainability.local.lime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

import org.kie.kogito.explainability.model.DataDistribution;
import org.kie.kogito.explainability.model.Feature;
import org.kie.kogito.explainability.model.Output;
import org.kie.kogito.explainability.model.PerturbationContext;
import org.kie.kogito.explainability.model.Prediction;
import org.kie.kogito.explainability.model.PredictionInputsDataDistribution;
import org.kie.kogito.explainability.model.PredictionProvider;
import org.kie.kogito.explainability.model.Type;
import org.kie.kogito.explainability.utils.DataUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provider for {@link HighScoreNumericFeatureZones} to be used to retain numeric feature points the {@link PredictionProvider}
 * is more confident with, to calculate feature intervals for perturbing numeric features (with boostrap).
 * <p>
 * see also {@link DataUtils#boostrapFeatureDistributions(DataDistribution, PerturbationContext, int, int, int, Map)}
 */
public class HighScoreNumericFeatureZonesProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(HighScoreNumericFeatureZonesProvider.class);

    private HighScoreNumericFeatureZonesProvider() {
    }

    /**
     * Get a map of feature-name -> high score feature zones. Predictions in data distribution are sorted by (descending)
     * score, then the (aggregated) mean score is calculated and all the data points that are associated with a prediction
     * having a score between the mean and the maximum are selected (feature-wise), with an associated tolerance
     * (the stdDev of the high score feature points).
     *
     * @param dataDistribution a data distribution
     * @param predictionProvider the model used to score the inputs
     * @param features the list of features to associate high score points with
     * @param maxNoOfSamples max no. of inputs used for discovering high score zones
     * @return a map feature name -> high score numeric feature zones
     */
    public static Map<String, HighScoreNumericFeatureZones> getHighScoreFeatureZones(DataDistribution dataDistribution,
            PredictionProvider predictionProvider, List<Feature> features, int maxNoOfSamples) {
        Map<String, HighScoreNumericFeatureZones> numericFeatureZonesMap = new HashMap<>();

        List<Prediction> scoreSortedPredictions = new ArrayList<>();
        try {
            scoreSortedPredictions.addAll(DataUtils.getScoreSortedPredictions(
                    predictionProvider, new PredictionInputsDataDistribution(dataDistribution.sample(maxNoOfSamples))));
        } catch (ExecutionException e) {
            LOGGER.error("Could not sort predictions by score {}", e.getMessage());
        } catch (InterruptedException e) {
            LOGGER.error("Interrupted while waiting for sorting predictions by score {}", e.getMessage());
            Thread.currentThread().interrupt();
        } catch (TimeoutException e) {
            LOGGER.error("Timed out while waiting for sorting predictions by score", e);
        }
        if (!scoreSortedPredictions.isEmpty()) {
            // calculate min, max and mean scores
            double max = scoreSortedPredictions.get(0).getOutput().getOutputs().stream().mapToDouble(Output::getScore).sum();
            double min = scoreSortedPredictions.get(scoreSortedPredictions.size() - 1).getOutput().getOutputs().stream()
                    .mapToDouble(Output::getScore).sum();
            if (max != min) {
                double threshold = scoreSortedPredictions.stream().map(
                        p -> p.getOutput().getOutputs().stream().mapToDouble(Output::getScore).sum())
                        .mapToDouble(d -> d).average().orElse((max + min) / 2);

                // filter out predictions whose score is in [min, threshold]
                scoreSortedPredictions = scoreSortedPredictions.stream().filter(p -> p.getOutput().getOutputs().stream()
                        .mapToDouble(Output::getScore).sum() > threshold).collect(Collectors.toList());

                for (int j = 0; j < features.size(); j++) {
                    Feature feature = features.get(j);
                    if (Type.NUMBER.equals(feature.getType())) {
                        int finalJ = j;
                        // get feature values associated with high score inputs
                        List<Double> topValues = scoreSortedPredictions.stream().map(prediction -> prediction.getInput()
                                .getFeatures().get(finalJ).getValue().asNumber())
                                .distinct().collect(Collectors.toList());

                        // get high score points and tolerance
                        double[] highScoreFeaturePoints = topValues.stream().flatMapToDouble(DoubleStream::of).toArray();
                        double center = DataUtils.getMean(highScoreFeaturePoints);
                        double tolerance = DataUtils.getStdDev(highScoreFeaturePoints, center) / 2;
                        HighScoreNumericFeatureZones highScoreNumericFeatureZones = new HighScoreNumericFeatureZones(highScoreFeaturePoints, tolerance);
                        numericFeatureZonesMap.put(feature.getName(), highScoreNumericFeatureZones);
                    }
                }
            }
        }
        return numericFeatureZonesMap;
    }
}
