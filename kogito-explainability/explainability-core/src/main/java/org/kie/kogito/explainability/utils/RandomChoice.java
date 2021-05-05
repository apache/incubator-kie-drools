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

package org.kie.kogito.explainability.utils;

import java.util.*;

public class RandomChoice<T> {
    NavigableMap<Double, T> sampleSet = new TreeMap<>();
    double totalWeights = 0.;

    /**
     * Initialize a weighted random sampler
     *
     * @param items an object list to draw samples from
     * @param weights a double list, the relative weighting of each object
     *
     */
    public RandomChoice(List<T> items, List<Double> weights) {
        if (items.size() != weights.size()) {
            throw new IllegalArgumentException(String.format(
                    "Length of weight vector does not match length of items: %d vs %d", weights.size(), items.size()));
        }

        // map each object into a treemap at its given weight
        // this partitions the range 0 to sum(weights) into chunks
        // where the chunk sizes correspond to the weights
        for (int i = 0; i < items.size(); i++) {
            if (weights.get(i) > 0) {
                totalWeights += weights.get(i);
                this.sampleSet.put(totalWeights, items.get(i));
            }
        }
    }

    /**
     * Initialize a uniform random sampler
     *
     * @param items an object list to uniformly draw samples from
     *
     */
    public RandomChoice(List<T> items) {
        for (int i = 0; i < items.size(); i++) {
            totalWeights += 1;
            this.sampleSet.put(totalWeights, items.get(i));
        }
    }

    /**
     * Draw samples with replacement from the specified objects according to the weights, if applicable
     *
     * @param n The number of samples to take
     *
     */
    public List<T> sample(int n, Random rn) {
        List<T> output = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            // generate a random number between 0 and sum(weights)
            double key = rn.nextDouble() * this.totalWeights;
            // find which chunk that number is part of
            output.add(this.sampleSet.higherEntry(key).getValue());
        }
        return output;
    }
}