package org.optaplanner.core.impl.heuristic.selector.common.nearby;

import java.util.Objects;
import java.util.Random;

/**
 * {@code P(x) = 2/m - 2x/m²}.
 * <p>
 * Cumulative probability: {@code F(x) = x(2m - x)/m²}.
 * <p>
 * Inverse cumulative probability: {@code F(p) = m(1 - (1 - p)^(1/2))}.
 */
public final class LinearDistributionNearbyRandom implements NearbyRandom {

    private final int sizeMaximum;

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

    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;
        if (other == null || getClass() != other.getClass())
            return false;
        LinearDistributionNearbyRandom that = (LinearDistributionNearbyRandom) other;
        return sizeMaximum == that.sizeMaximum;
    }

    @Override
    public int hashCode() {
        return Objects.hash(sizeMaximum);
    }
}
