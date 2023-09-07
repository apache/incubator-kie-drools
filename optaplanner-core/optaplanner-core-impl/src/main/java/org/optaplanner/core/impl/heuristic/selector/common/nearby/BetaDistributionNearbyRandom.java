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

import org.apache.commons.math3.distribution.BetaDistribution;

public final class BetaDistributionNearbyRandom implements NearbyRandom {

    private final BetaDistribution betaDistribution;

    public BetaDistributionNearbyRandom(double betaDistributionAlpha, double betaDistributionBeta) {
        if (betaDistributionAlpha <= 0) {
            throw new IllegalArgumentException("The betaDistributionAlpha (" + betaDistributionAlpha
                    + ") must be greater than 0.");
        }
        if (betaDistributionBeta <= 0) {
            throw new IllegalArgumentException("The betaDistributionBeta (" + betaDistributionBeta
                    + ") must be greater than 0.");
        }
        betaDistribution = new BetaDistribution(betaDistributionAlpha, betaDistributionBeta);
    }

    @Override
    public int nextInt(Random random, int nearbySize) {
        double d = betaDistribution.inverseCumulativeProbability(random.nextDouble());
        int next = (int) (d * nearbySize);
        // The method inverseCumulativeProbability() might return 1.0
        if (next >= nearbySize) {
            next = nearbySize - 1;
        }
        return next;
    }

    @Override
    public int getOverallSizeMaximum() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;
        if (other == null || getClass() != other.getClass())
            return false;
        BetaDistributionNearbyRandom that = (BetaDistributionNearbyRandom) other;
        return Objects.equals(betaDistribution.getAlpha(), that.betaDistribution.getAlpha())
                && Objects.equals(betaDistribution.getBeta(), that.betaDistribution.getBeta());
    }

    @Override
    public int hashCode() {
        return Objects.hash(betaDistribution.getAlpha(), betaDistribution.getBeta());
    }
}
