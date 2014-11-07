/*
 * Copyright 2014 JBoss Inc
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
 * P(x) = 3(m - x)²/k³
 * Cumulative probability F(x) = 1 - (1 - x/k)³
 * Inverse cumulative probability F(p) = m(1 - (1 - p)^(1/3))
 */
public class ParabolicDistributionNearbyRandom implements NearbyRandom {

    protected final int sizeMaximum;

    public ParabolicDistributionNearbyRandom(int sizeMaximum) {
        this.sizeMaximum = sizeMaximum;
        if (sizeMaximum < 1) {
            throw new IllegalArgumentException("The maximum (" + sizeMaximum
                    + ") must be at least 1.");
        }
    }

    @Override
    public int nextInt(Random random, int n) {
        int m = sizeMaximum <= n ? sizeMaximum : n;
        double p = random.nextDouble();
        double x = m * (1.0 - Math.pow(1.0 - p, 1.0 / 3.0));
        int next = (int) x;
        // Due to a rounding error it might return m
        if (next >= m) {
            next = m - 1;
        }
        return next;
    }

}
