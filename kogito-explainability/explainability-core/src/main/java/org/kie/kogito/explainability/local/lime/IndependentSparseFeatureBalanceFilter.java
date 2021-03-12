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

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.kie.kogito.explainability.model.Feature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Filter for feature weight coefficients.
 * It attenuates feature weight coefficients:
 * - proportionally to their respective class balance
 * - inverse proportionally to the number of existing features
 * <p>
 * We consider a sparse feature to be better for training the classifier accurately when it doesn't present a good
 * balance of 1s and 0s values with respect to 0 and 1 predictions. In fact such features would hardly generate a
 * well fitting classifier, if taken in isolation.
 * The generated coefficients are proportional with respect to the no. of features as class balance becomes more
 * impacting when the no. of features is low.
 */
class IndependentSparseFeatureBalanceFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(IndependentSparseFeatureBalanceFilter.class);

    IndependentSparseFeatureBalanceFilter() {
    }

    /**
     * Filter feature weight {@code coefficients} by attenuating them according to class balance and number of features.
     * Resulting {@code coefficients} are changed according to the following formula:
     * {@code c[i] = Math.tanh((1e-2 + Math.abs(classBalanceForZero - 0.5) + Math.abs(classBalanceForOne - 0.5)) + ts / 10d)}
     *
     * @param linearizedTargetInputFeatures no of features
     * @param trainingSet training set for the linear classifier
     */
    void apply(double[] coefficients, List<Feature> linearizedTargetInputFeatures,
            List<Pair<double[], Double>> trainingSet) {
        if (coefficients.length != linearizedTargetInputFeatures.size()) {
            LOGGER.warn("coefficients size {} ≠ features size {}, not filtering", coefficients.length,
                    linearizedTargetInputFeatures.size());
            return;
        }
        if (trainingSet.isEmpty()) {
            LOGGER.debug("trainingSet is empty");
            return;
        }
        int ts = linearizedTargetInputFeatures.size();
        // calculate per feature class balance
        double[] zeroPredicted = new double[ts];
        double[] onePredicted = new double[ts];
        for (Pair<double[], Double> sample : trainingSet) {
            double[] sparseVector = sample.getKey();
            for (int i = 0; i < sparseVector.length; i++) {
                double inputValue = sparseVector[i];
                Double outputValue = sample.getValue();
                if (1 == outputValue) {
                    onePredicted[i] += inputValue;
                } else {
                    zeroPredicted[i] += inputValue;
                }
            }
        }
        zeroPredicted = Arrays.stream(zeroPredicted).map(d -> d / trainingSet.size()).toArray();
        onePredicted = Arrays.stream(onePredicted).map(d -> d / trainingSet.size()).toArray();
        for (int i = 0; i < coefficients.length; i++) {
            // calculate distance from the perfect balance (high is good)
            double zeroDistance = Math.abs(0.5 - zeroPredicted[i]);
            double oneDistance = Math.abs(0.5 - onePredicted[i]);
            // coefficient is proportional to distance and to the number of features (between 0 and 1)
            double zm = Math.tanh((1e-2 + zeroDistance + oneDistance) + ts / 10d);
            coefficients[i] *= zm;
        }
    }
}
