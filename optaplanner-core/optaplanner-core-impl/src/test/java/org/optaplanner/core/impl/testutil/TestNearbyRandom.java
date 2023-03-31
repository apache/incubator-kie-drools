package org.optaplanner.core.impl.testutil;

import java.util.Random;

import org.optaplanner.core.impl.heuristic.selector.common.nearby.NearbyRandom;

/**
 * Simply returns next integer produced by the given "working" random, which is expected to be a {@link TestRandom} under
 * control of the test.
 */
public class TestNearbyRandom implements NearbyRandom {

    @Override
    public int nextInt(Random random, int nearbySize) {
        return random.nextInt(nearbySize);
    }

    @Override
    public int getOverallSizeMaximum() {
        // Not yet needed.
        return Integer.MAX_VALUE;
    }
}
