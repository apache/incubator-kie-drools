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

}
