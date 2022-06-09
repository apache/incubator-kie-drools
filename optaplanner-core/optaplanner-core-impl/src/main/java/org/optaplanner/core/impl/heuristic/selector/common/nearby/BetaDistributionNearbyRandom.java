package org.optaplanner.core.impl.heuristic.selector.common.nearby;

import java.util.Random;

import org.apache.commons.math3.distribution.BetaDistribution;

public class BetaDistributionNearbyRandom implements NearbyRandom {

    protected final BetaDistribution betaDistribution;

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

}
