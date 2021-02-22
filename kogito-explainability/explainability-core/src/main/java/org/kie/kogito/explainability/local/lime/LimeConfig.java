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

import java.security.SecureRandom;

import org.kie.kogito.explainability.model.EncodingParams;
import org.kie.kogito.explainability.model.PerturbationContext;

/**
 * Lime explainer configuration parameters.
 */
public class LimeConfig {

    private static final int DEFAULT_NO_OF_SAMPLES = 300;
    private static final double DEFAULT_SEPARABLE_DATASET_RATIO = 0.99;
    public static final int DEFAULT_NO_OF_RETRIES = 3;
    private static final boolean DEFAULT_ADAPT_DATASET_VARIANCE = false;
    private static final double DEFAULT_PROXIMITY_KERNEL_WIDTH = 0.675;
    private static final double DEFAULT_ENCODING_CLUSTER_THRESHOLD = 0.1;
    private static final double DEFAULT_ENCODING_GAUSSIAN_FILTER_WIDTH = 1;

    private double separableDatasetRatio = DEFAULT_SEPARABLE_DATASET_RATIO;

    /**
     * No. of samples to be generated for the local linear model training.
     */
    private int noOfSamples = DEFAULT_NO_OF_SAMPLES;

    /**
     * No. of retries while trying to find a (linearly) separable dataset.
     */
    private int noOfRetries = DEFAULT_NO_OF_RETRIES;

    /**
     * Context object for perturbing features.
     */
    private PerturbationContext perturbationContext = new PerturbationContext(new SecureRandom(), 1);

    /**
     * Whether the explainer should adapt the variance in the generated (perturbed) data when it's not separable.
     */
    private boolean adaptDatasetVariance = DEFAULT_ADAPT_DATASET_VARIANCE;

    /**
     * The width of the kernel used to calculate proximity of sparse vector instances.
     */
    private double proximityKernelWidth = DEFAULT_PROXIMITY_KERNEL_WIDTH;

    /**
     * {@link EncodingParams} used to perform sparse encoding for LIME.
     */
    private EncodingParams encodingParams = new EncodingParams(DEFAULT_ENCODING_GAUSSIAN_FILTER_WIDTH, DEFAULT_ENCODING_CLUSTER_THRESHOLD);

    public LimeConfig withSeparableDatasetRatio(double separableDatasetRatio) {
        this.separableDatasetRatio = separableDatasetRatio;
        return this;
    }

    public LimeConfig withPerturbationContext(PerturbationContext perturbationContext) {
        this.perturbationContext = perturbationContext;
        return this;
    }

    public LimeConfig withAdaptiveVariance(boolean adaptDatasetVariance) {
        this.adaptDatasetVariance = adaptDatasetVariance;
        return this;
    }

    public LimeConfig withRetries(int noOfRetries) {
        this.noOfRetries = noOfRetries;
        return this;
    }

    public LimeConfig withSamples(int noOfSamples) {
        this.noOfSamples = noOfSamples;
        return this;
    }

    public LimeConfig withProximityKernelWidth(double proximityKernelWidth) {
        this.proximityKernelWidth = proximityKernelWidth;
        return this;
    }

    public int getNoOfRetries() {
        return noOfRetries;
    }

    public int getNoOfSamples() {
        return noOfSamples;
    }

    public PerturbationContext getPerturbationContext() {
        return perturbationContext;
    }

    public boolean adaptDatasetVariance() {
        return adaptDatasetVariance;
    }

    public double getSeparableDatasetRatio() {
        return separableDatasetRatio;
    }

    public double getProximityKernelWidth() {
        return proximityKernelWidth;
    }

    public EncodingParams getEncodingParams() {
        return encodingParams;
    }

    public LimeConfig withEncodingParams(EncodingParams encodingParams) {
        this.encodingParams = encodingParams;
        return this;
    }
}
