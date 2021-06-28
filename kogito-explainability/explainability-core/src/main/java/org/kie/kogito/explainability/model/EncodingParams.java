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
package org.kie.kogito.explainability.model;

/**
 * Params for {@link Type}-aware encoding.
 *
 * See {@link Type#encode(EncodingParams, Value, Value[])}
 */
public class EncodingParams {

    private final double numericTypeClusterGaussianFilterWidth;
    private final double numericTypeClusterThreshold;

    public EncodingParams(double numericTypeClusterGaussianFilterWidth, double numericTypeClusterThreshold) {
        this.numericTypeClusterGaussianFilterWidth = numericTypeClusterGaussianFilterWidth;
        this.numericTypeClusterThreshold = numericTypeClusterThreshold;
    }

    /**
     * The width of the gaussian filter used in clustering for {@link Type#encode(EncodingParams, Value, Value[])} of
     * {@code Type.NUMBER} {@link Value}s.
     *
     * @return the width of the gaussian filter
     */
    public double getNumericTypeClusterGaussianFilterWidth() {
        return numericTypeClusterGaussianFilterWidth;
    }

    /**
     * The threshold used in clustering for {@link Type#encode(EncodingParams, Value, Value[])} of {@code Type.NUMBER}
     * {@link Value}s.
     *
     * @return the cluster threshold
     */
    public double getNumericTypeClusterThreshold() {
        return numericTypeClusterThreshold;
    }

    @Override
    public String toString() {
        return "EncodingParams{" +
                "numericTypeClusterGaussianFilterWidth=" + numericTypeClusterGaussianFilterWidth +
                ", numericTypeClusterThreshold=" + numericTypeClusterThreshold +
                '}';
    }
}
