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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ShapStatisticsTest {

    int numSubsetSizes = 5;
    int largestPairedSubsetSize = 3;
    int[] numSubsetsAtSize = { 1, 3, 5, 3, 1 };
    int numSamplesRemaining = 10;

    double[] weightOfSubsetSize = { .1, .3, .5, .3, .1 };
    double[] finalRemainingWeights = { .2, .4, .6, .4, .2 };
    double[] remainingWeights = { .02, .04, .06, .04, .02 };

    // subset stats getters and setters tests ==================
    @Test
    void getNumSubsetSizesTest() {
        ShapStatistics shapStats = new ShapStatistics(numSubsetSizes, largestPairedSubsetSize,
                numSubsetsAtSize, numSamplesRemaining);
        assertEquals(numSubsetSizes, shapStats.getNumSubsetSizes());
    }

    @Test
    void getNumSubsetsAtSizeTest() {
        ShapStatistics shapStats = new ShapStatistics(numSubsetSizes, largestPairedSubsetSize,
                numSubsetsAtSize, numSamplesRemaining);
        assertArrayEquals(numSubsetsAtSize, shapStats.getNumSubsetsAtSize());
    }

    @Test
    void getLargestPairedSubsetSizeTest() {
        ShapStatistics shapStats = new ShapStatistics(numSubsetSizes, largestPairedSubsetSize,
                numSubsetsAtSize, numSamplesRemaining);
        assertEquals(largestPairedSubsetSize, shapStats.getLargestPairedSubsetSize());
    }

    // num full subset operations tests ======================
    @Test
    void numFullSubsetsTest() {
        ShapStatistics shapStats = new ShapStatistics(numSubsetSizes, largestPairedSubsetSize,
                numSubsetsAtSize, numSamplesRemaining);
        assertEquals(0, shapStats.getNumFullSubsets());
        shapStats.incrementNumFullSubsets();
        assertEquals(1, shapStats.getNumFullSubsets());
        shapStats.incrementNumFullSubsets();
        assertEquals(2, shapStats.getNumFullSubsets());
    }

    // sample stats getters and setters tests ======================
    @Test
    void weightOfSubsetSizeTest() {
        ShapStatistics shapStats = new ShapStatistics(numSubsetSizes, largestPairedSubsetSize,
                numSubsetsAtSize, numSamplesRemaining);
        shapStats.setWeightOfSubsetSize(weightOfSubsetSize);
        assertArrayEquals(weightOfSubsetSize, shapStats.getWeightOfSubsetSize());
    }

    // remaining weight operations tests =============
    @Test
    void remainingWeightsTest() {
        ShapStatistics shapStats = new ShapStatistics(numSubsetSizes, largestPairedSubsetSize,
                numSubsetsAtSize, numSamplesRemaining);
        shapStats.setRemainingWeights(remainingWeights);
        assertArrayEquals(remainingWeights, shapStats.getRemainingWeights());
    }

    @Test
    void finalRemainingWeightsTest() {
        ShapStatistics shapStats = new ShapStatistics(numSubsetSizes, largestPairedSubsetSize,
                numSubsetsAtSize, numSamplesRemaining);
        shapStats.setFinalRemainingWeights(finalRemainingWeights);
        assertArrayEquals(finalRemainingWeights, shapStats.getFinalRemainingWeights());
    }

    // remaining sample operations tests ============
    @Test
    void numSamplesRemainingTest() {
        ShapStatistics shapStats = new ShapStatistics(numSubsetSizes, largestPairedSubsetSize,
                numSubsetsAtSize, numSamplesRemaining);
        assertEquals(numSamplesRemaining, shapStats.getNumSamplesRemaining());
        shapStats.decreaseNumSamplesRemainingBy(3);
        assertEquals(numSamplesRemaining - 3, shapStats.getNumSamplesRemaining());
        shapStats.decreaseNumSamplesRemainingBy(-5);
        assertEquals(numSamplesRemaining - 3 + 5, shapStats.getNumSamplesRemaining());
    }
}