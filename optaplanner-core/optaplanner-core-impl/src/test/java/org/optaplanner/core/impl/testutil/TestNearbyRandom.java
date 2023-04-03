package org.optaplanner.core.impl.testutil;

import java.util.Objects;
import java.util.Random;

import org.optaplanner.core.impl.heuristic.selector.common.nearby.NearbyRandom;

/**
 * Simply returns next integer produced by the given "working" random, which is expected to be a {@link TestRandom} under
 * control of the test.
 */
public class TestNearbyRandom implements NearbyRandom {

    private final int overallSizeMaximum;

    public TestNearbyRandom() {
        this(Integer.MAX_VALUE);
    }

    public TestNearbyRandom(int overallSizeMaximum) {
        this.overallSizeMaximum = overallSizeMaximum;
    }

    public static TestNearbyRandom withDistributionSizeMaximum(int distributionSizeMaximum) {
        return new TestNearbyRandom(distributionSizeMaximum);
    }

    @Override
    public int nextInt(Random random, int nearbySize) {
        return random.nextInt(nearbySize);
    }

    @Override
    public int getOverallSizeMaximum() {
        return overallSizeMaximum;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        TestNearbyRandom that = (TestNearbyRandom) o;
        return overallSizeMaximum == that.overallSizeMaximum;
    }

    @Override
    public int hashCode() {
        return Objects.hash(overallSizeMaximum);
    }

}
