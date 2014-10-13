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

public class BlockDistributionNearbyRandom implements NearbyRandom {

    protected final int sizeMinimum;
    protected final int sizeMaximum;
    protected final double sizeRatio;

    protected final double uniformDistributionProbability;

    public BlockDistributionNearbyRandom(int sizeMinimum, int sizeMaximum, double sizeRatio,
            double uniformDistributionProbability) {
        this.sizeMinimum = sizeMinimum;
        this.sizeMaximum = sizeMaximum;
        this.sizeRatio = sizeRatio;
        this.uniformDistributionProbability = uniformDistributionProbability;
        if (sizeMinimum < 0) {
            throw new IllegalArgumentException("The sizeMinimum (" + sizeMinimum
                    + ") must be at least 0.");
        }
        if (sizeMaximum < sizeMinimum) {
            throw new IllegalArgumentException("The sizeMaximum (" + sizeMaximum
                    + ") must be at least the sizeMinimum (" + sizeMinimum + ").");

        }
        if (sizeRatio < 0.0 || sizeRatio > 1.0) {
            throw new IllegalArgumentException("The sizeRatio (" + sizeRatio
                    + ") must be between 0.0 and 1.0.");
        }
        if (uniformDistributionProbability < 0.0 || uniformDistributionProbability > 1.0) {
            throw new IllegalArgumentException("The uniformDistributionProbability (" + uniformDistributionProbability
                    + ") must be between 0.0 and 1.0.");
        }
    }

    @Override
    public int nextInt(Random random, int n) {
        if (uniformDistributionProbability > 0.0) {
            if (random.nextDouble() < uniformDistributionProbability) {
                return random.nextInt(n);
            }
        }
        int size;
        if (sizeRatio < 1.0) {
            size = (int) (n * sizeRatio);
            if (size < sizeMinimum) {
                size = sizeMinimum;
                if (size > n) {
                    size = n;
                }
            }
        } else {
            size = n;
        }
        if (size > sizeMaximum) {
            size = sizeMaximum;
        }
        return random.nextInt(size);
    }

}
