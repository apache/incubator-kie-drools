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

public class ShapStatistics {

    // subset stats =================================
    private final int numSubsetSizes;
    private int numFullSubsets;
    private final int[] numSubsetsAtSize;
    private final int largestPairedSubsetSize;

    // sample stats
    private double[] remainingWeights;
    private double[] weightOfSubsetSize;
    private double[] finalRemainingWeights;
    private int numSamplesRemaining;

    /**
     * Define a ShapStatistics object. This carries necessary information for a single ShapKernelExplainer
     * explanation run, and a new one is created per explanation.
     * This is used internally by the ShapKernelExplainer, and should never need to be directly
     * interacted with by the user.
     *
     * @param numSubsetSizes: A subset is a specific selection of included features. The size of the
     *        subset is the number of features that are included. The number of subset
     *        sizes is the number of different subset sizes possible,
     *        not including complement subset sizes.
     * @param largestPairedSubsetSize: the largest subset that has a complement subset
     * @param numSubsetsAtSize: the size of each subset (ie, how many different
     *        permutations of n features are there?)
     * @param numSamplesRemaining: A tracker of the number of available samples we have left
     */
    public ShapStatistics(int numSubsetSizes, int largestPairedSubsetSize, int[] numSubsetsAtSize, int numSamplesRemaining) {
        this.numSubsetSizes = numSubsetSizes;
        this.numSubsetsAtSize = numSubsetsAtSize;
        this.largestPairedSubsetSize = largestPairedSubsetSize;
        this.numSamplesRemaining = numSamplesRemaining;
        this.numFullSubsets = 0;
    }

    // subset stats getters and setters
    public int getNumSubsetSizes() {
        return numSubsetSizes;
    }

    public int[] getNumSubsetsAtSize() {
        return numSubsetsAtSize;
    }

    public int getLargestPairedSubsetSize() {
        return largestPairedSubsetSize;
    }

    // num full subset operations ======================
    public int getNumFullSubsets() {
        return numFullSubsets;
    }

    public void incrementNumFullSubsets() {
        this.numFullSubsets += 1;
    }

    // sample stats getters and setters ======================
    public double[] getWeightOfSubsetSize() {
        return weightOfSubsetSize;
    }

    public void setWeightOfSubsetSize(double[] weightOfSubsetSize) {
        this.weightOfSubsetSize = weightOfSubsetSize;
    }

    // remaining weight operations =============
    public double[] getRemainingWeights() {
        return remainingWeights;
    }

    public void setRemainingWeights(double[] remainingWeights) {
        this.remainingWeights = remainingWeights;
    }

    public double[] getFinalRemainingWeights() {
        return finalRemainingWeights;
    }

    public void setFinalRemainingWeights(double[] finalRemainingWeights) {
        this.finalRemainingWeights = finalRemainingWeights;
    }

    // remaining sample operations ============
    public int getNumSamplesRemaining() {
        return numSamplesRemaining;
    }

    public void decreaseNumSamplesRemainingBy(int value) {
        this.numSamplesRemaining -= value;
    }
}
