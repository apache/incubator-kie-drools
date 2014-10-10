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

    protected final int blockSizeMinimum;
    protected final int blockSizeMaximum;
    protected final double blockRatio;

    protected final double uniformDistributionProbability;

    public BlockDistributionNearbyRandom(int blockSizeMinimum, int blockSizeMaximum, double blockRatio,
            double uniformDistributionProbability) {
        this.blockSizeMinimum = blockSizeMinimum;
        this.blockSizeMaximum = blockSizeMaximum;
        this.blockRatio = blockRatio;
        this.uniformDistributionProbability = uniformDistributionProbability;
        if (blockSizeMinimum < 0) {
            throw new IllegalArgumentException("The blockSizeMinimum (" + blockSizeMinimum
                    + ") must be at least 0.");
        }
        if (blockSizeMaximum < blockSizeMinimum) {
            throw new IllegalArgumentException("The blockSizeMaximum (" + blockSizeMaximum
                    + ") must be at least the blockSizeMinimum (" + blockSizeMinimum + ").");

        }
        if (blockRatio < 0.0 || blockRatio > 1.0) {
            throw new IllegalArgumentException("The blockRatio (" + blockRatio
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
        if (blockRatio < 1.0) {
            size = (int) (n * blockRatio);
            if (size < blockSizeMinimum) {
                size = blockSizeMinimum;
                if (size > n) {
                    size = n;
                }
            }
        } else {
            size = n;
        }
        if (size > blockSizeMaximum) {
            size = blockSizeMaximum;
        }
        return random.nextInt(size);
    }

}
