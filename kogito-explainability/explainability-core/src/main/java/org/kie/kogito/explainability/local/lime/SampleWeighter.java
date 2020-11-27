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
package org.kie.kogito.explainability.local.lime;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.kie.kogito.explainability.model.Feature;
import org.kie.kogito.explainability.utils.DataUtils;

/**
 * Utility class to generate weights for the LIME encoded training set, given a prediction.
 */
class SampleWeighter {

    private static final double SIGMA = 0.75;

    /**
     * Obtain sample weights for a training set, given a list of target input features to compare with.
     * @param targetInputFeatures target input features
     * @param training the (sparse) training set
     * @return a eeight for each sample in the training set
     */
    static double[] getSampleWeights(List<Feature> targetInputFeatures, Collection<Pair<double[], Double>> training) {
        int noOfFeatures = targetInputFeatures.size();
        double[] x = new double[noOfFeatures];
        Arrays.fill(x, 1);

        return training.stream().map(Pair::getLeft)
                .map(d -> DataUtils.euclideanDistance(x, d)) // calculate euclidean distance between target and sample points
                .map(d -> DataUtils.exponentialSmoothingKernel(d, SIGMA * Math.sqrt(noOfFeatures))) // transform distance into proximity using an exponential smoothing kernel
                .mapToDouble(Double::doubleValue).toArray(); // output to an array
    }
}
