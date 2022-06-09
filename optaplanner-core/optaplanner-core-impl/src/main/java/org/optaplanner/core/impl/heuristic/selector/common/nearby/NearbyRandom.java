package org.optaplanner.core.impl.heuristic.selector.common.nearby;

import java.util.Random;

/**
 * Strategy pattern to select a index of a nearby ordered value range according to a probability distribution.
 */
public interface NearbyRandom {

    /**
     *
     * @param random never null
     * @param nearbySize never negative. The number of available values to select from.
     *        Normally this is the size of the value range for a non-chained variable
     *        and the size of the value range (= size of the entity list) minus 1 for a chained variable.
     * @return {@code 0 <= x < nearbySize}
     */
    int nextInt(Random random, int nearbySize);

    /**
     * Used to limit the RAM memory size of the nearby distance matrix.
     *
     * @return one more than the maximum number that {@link #nextInt(Random, int)} can return,
     *         {@link Integer#MAX_VALUE} if there is none
     */
    int getOverallSizeMaximum();

}
