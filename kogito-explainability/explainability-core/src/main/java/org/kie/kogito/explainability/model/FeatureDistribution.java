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
package org.kie.kogito.explainability.model;

/**
 * The data distribution for a given feature.
 */
public class FeatureDistribution {

    private final double min;
    private final double max;
    private final double mean;
    private final double stdDev;

    public FeatureDistribution(double min, double max, double mean, double stdDev) {
        this.min = min;
        this.max = max;
        this.mean = mean;
        this.stdDev = stdDev;
    }

    /**
     * Get minimum value for this feature
     *
     * @return the min value
     */
    public double getMin() {
        return min;
    }

    /**
     * Get the maximum value for this feature
     *
     * @return the max value
     */
    public double getMax() {
        return max;
    }

    /**
     * Get the mean value for this feature
     *
     * @return the mean value
     */
    public double getMean() {
        return mean;
    }

    /**
     * Get the standard deviation for this feature
     *
     * @return the standard deviation
     */
    public double getStdDev() {
        return stdDev;
    }
}
