/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.core.impl.heuristic.selector.common.nearby;

import java.util.Random;

/**
 * {@code P(x) = 2/m - 2x/m²}.
 * <p>
 * Cumulative probability: {@code F(x) = x(2m - x)/m²}.
 * <p>
 * Inverse cumulative probability: {@code F(p) = m(1 - (1 - p)^(1/2))}.
 */
public class LinearDistributionNearbyRandom implements NearbyRandom {

    protected final int sizeMaximum;

    public LinearDistributionNearbyRandom(int sizeMaximum) {
        this.sizeMaximum = sizeMaximum;
        if (sizeMaximum < 1) {
            throw new IllegalArgumentException("The maximum (" + sizeMaximum
                    + ") must be at least 1.");
        }
    }

    @Override
    public int nextInt(Random random, int nearbySize) {
        int m = sizeMaximum <= nearbySize ? sizeMaximum : nearbySize;
        double p = random.nextDouble();
        double x = m * (1.0 - Math.sqrt(1.0 - p));
        int next = (int) x;
        // Due to a rounding error it might return m
        if (next >= m) {
            next = m - 1;
        }
        return next;
    }

    @Override
    public int getOverallSizeMaximum() {
        return sizeMaximum;
    }

}
