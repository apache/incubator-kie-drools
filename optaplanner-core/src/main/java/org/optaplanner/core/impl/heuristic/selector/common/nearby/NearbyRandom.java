/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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
