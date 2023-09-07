/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.optaplanner.core.impl.heuristic.selector.common.nearby;

import java.util.Objects;
import java.util.Random;

public final class BlockDistributionNearbyRandom implements NearbyRandom {

    private final int sizeMinimum;
    private final int sizeMaximum;
    private final double sizeRatio;
    private final double uniformDistributionProbability;

    public BlockDistributionNearbyRandom(int sizeMinimum, int sizeMaximum, double sizeRatio,
            double uniformDistributionProbability) {
        this.sizeMinimum = sizeMinimum;
        this.sizeMaximum = sizeMaximum;
        this.sizeRatio = sizeRatio;
        this.uniformDistributionProbability = uniformDistributionProbability;
        if (sizeMinimum < 1) {
            throw new IllegalArgumentException("The sizeMinimum (" + sizeMinimum
                    + ") must be at least 1.");
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
    public int nextInt(Random random, int nearbySize) {
        if (uniformDistributionProbability > 0.0) {
            if (random.nextDouble() < uniformDistributionProbability) {
                return random.nextInt(nearbySize);
            }
        }
        int size;
        if (sizeRatio < 1.0) {
            size = (int) (nearbySize * sizeRatio);
            if (size < sizeMinimum) {
                size = sizeMinimum;
                if (size > nearbySize) {
                    size = nearbySize;
                }
            }
        } else {
            size = nearbySize;
        }
        if (size > sizeMaximum) {
            size = sizeMaximum;
        }
        return random.nextInt(size);
    }

    @Override
    public int getOverallSizeMaximum() {
        if (uniformDistributionProbability > 0.0) {
            return Integer.MAX_VALUE;
        }
        return sizeMaximum;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;
        if (other == null || getClass() != other.getClass())
            return false;
        BlockDistributionNearbyRandom that = (BlockDistributionNearbyRandom) other;
        return sizeMinimum == that.sizeMinimum && sizeMaximum == that.sizeMaximum
                && Double.compare(that.sizeRatio, sizeRatio) == 0
                && Double.compare(that.uniformDistributionProbability, uniformDistributionProbability) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(sizeMinimum, sizeMaximum, sizeRatio, uniformDistributionProbability);
    }
}
