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

package org.kie.kogito.explainability.local.shap;

import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;

import org.kie.kogito.explainability.model.PerturbationContext;
import org.kie.kogito.explainability.model.PredictionInput;
import org.kie.kogito.explainability.utils.MatrixUtils;

public class ShapConfig {
    public enum LinkType {
        LOGIT,
        IDENTITY
    }

    private final LinkType link;
    private final Integer nSamples;
    private final double confidence;
    private final PerturbationContext pc;
    private final Executor executor;
    private final List<PredictionInput> background;
    private final double[][] backgroundMatrix;

    /**
     * Create a ShapConfig instance. This sets the configuration of the SHAP explainer.
     *
     * @param link: enum, either LOGIT or IDENTITY.
     *        The link function is used as follows: link(modelOutput) = sum(shapValues)
     *        - If you want the shapValues to sum to the exact modelOutput, use IDENTITY
     *        - If your model outputs probabilities and you want the shap values to
     *        use log-odds units, use LOGIT
     * @param background: The background data used to define the context for any particular model
     *        output. This should be a representative sample of the data, to provide a useful
     *        null background for the model. Automated guidance for background data selection
     *        is a WIP.
     * @param pc: PerturbationContext for random number generator
     * @param executor: The executor to use for the Shap CompletableFutures
     * @param nSamples: int, the number of data samples to run when computing shap values
     */
    protected ShapConfig(LinkType link, List<PredictionInput> background, PerturbationContext pc, Executor executor, Integer nSamples, double confidence) {
        this.link = link;
        this.background = background;
        this.backgroundMatrix = MatrixUtils.matrixFromPredictionInput(background);
        this.pc = pc;
        this.executor = executor;
        this.nSamples = nSamples;
        this.confidence = confidence;
    }

    public static Builder builder() {
        return new Builder();
    }

    // builder class for the ShapConfig
    public static class Builder {
        //mandatory
        private LinkType builderLink;
        private List<PredictionInput> builderBackground;

        // optional
        private Executor builderExecutor = ForkJoinPool.commonPool();
        private Integer builderNSamples = null;
        private double builderConfidence = .95;
        private PerturbationContext builderPC = new PerturbationContext(new SecureRandom(), 0);

        private Builder() {
        }

        /**
         * Add a link function to the builder: mandatory
         *
         * @param link: enum, either LOGIT or IDENTITY.
         *        The link function is used as follows: link(modelOutput) = sum(shapValues)
         *        - If you want the shapValues to sum to the exact modelOutput, use IDENTITY
         *        - If your model outputs probabilities and you want the shap values to
         *        use log-odds units, use LOGIT
         *
         * @return Builder
         */
        public Builder withLink(LinkType link) {
            this.builderLink = link;
            return this;
        }

        /**
         * Add background data to the builder
         *
         * @param background: The background data used to define the context for any particular model
         *        output. This should be a representative sample of the data, to provide a useful
         *        null background for the model. Automated guidance for background data selection
         *        is a WIP.
         *
         * @return Builder
         */
        public Builder withBackground(List<PredictionInput> background) {
            this.builderBackground = background;
            return this;
        }

        /**
         * Add an executor to the builder
         *
         * @param executor: The executor to use for the Shap CompletableFutures. By default, ForkJoinPool.commonPool
         * 
         * @return Builder
         */
        public Builder withExecutor(Executor executor) {
            this.builderExecutor = executor;
            return this;
        }

        /**
         * Add nsamples to the builder
         *
         * @param nSamples: int, the number of data samples to run when computing
         *        shap values. Default is 2048 + 2*nfeatures
         *
         * @return Builder
         */
        public Builder withNSamples(Integer nSamples) {
            this.builderNSamples = nSamples;
            return this;
        }

        /**
         * Add confidence interval to the builder
         *
         * @param confidence: double, the desired confidence interval to report in the ShapValues, default is .95 for
         *        a 95% confidence interval
         *
         * @return Builder
         */
        public Builder withConfidence(double confidence) {
            this.builderConfidence = confidence;
            return this;
        }

        /**
         * Add a random number generator to the builder
         *
         * @param pc: PerturbationContext to hold random number generator, Default is SecureRandom
         *
         * @return Builder
         */
        public Builder withPC(PerturbationContext pc) {
            this.builderPC = pc;
            return this;
        }

        /**
         * Build
         *
         * @return ShapConfig
         */
        public ShapConfig build() {
            if (this.builderLink == null || this.builderBackground == null) {
                throw new IllegalArgumentException("Both a link function and background must be" +
                        "provided to the ShapConfig");
            }
            if (this.builderBackground.isEmpty()) {
                throw new IllegalArgumentException("Background data list cannot be empty.");
            }
            return new ShapConfig(this.builderLink, this.builderBackground, this.builderPC, this.builderExecutor, this.builderNSamples, this.builderConfidence);
        }
    }

    /**
     * getters and setters for the various attributes
     */
    public LinkType getLink() {
        return this.link;
    }

    public PerturbationContext getPC() {
        return this.pc;
    }

    public double[][] getBackgroundMatrix() {
        return this.backgroundMatrix;
    }

    public List<PredictionInput> getBackground() {
        return this.background;
    }

    public Executor getExecutor() {
        return this.executor;
    }

    public Optional<Integer> getNSamples() {
        return Optional.ofNullable(this.nSamples);
    }

    public double getConfidence() {
        return this.confidence;
    }
}
