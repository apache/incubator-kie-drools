/*
 * Copyright 2010 JBoss Inc
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

package org.optaplanner.core.api.score;

import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;

/**
 * A Score is result of the score function (AKA fitness function) on a single possible solution.
 * <p/>
 * Implementations must be immutable.
 * <p/>
 * Implementations are allowed to optionally implement Pareto comparison
 * and therefore slightly violate the transitive requirement of {@link Comparable#compareTo(Object)}.
 * @see AbstractScore
 * @see HardSoftScore
 */
public interface Score<S extends Score> extends Comparable<S> {

    /**
     * Returns a Score whose value is (this + augment).
     * @param augment value to be added to this Score
     * @return this + augment
     */
    S add(S augment);

    /**
     * Returns a Score whose value is (this - subtrahend).
     * @param subtrahend value to be subtracted from this Score
     * @return this - subtrahend, rounded as necessary
     */
    S subtract(S subtrahend);

    /**
     * Returns a Score whose value is (this * multiplicand).
     * When rounding is needed, it should be floored (as defined by {@link Math#floor(double)}.
     * <p>/>
     * If the implementation has a scale/precision, then the unspecified scale/precision of the double multiplicand
     * should have no impact on the returned scale/precision.
     * @param multiplicand value to be multiplied by this Score.
     * @return this * multiplicand
     */
    S multiply(double multiplicand);

    /**
     * Returns a Score whose value is (this / divisor).
     * When rounding is needed, it should be floored (as defined by {@link Math#floor(double)}.
     * <p>/>
     * If the implementation has a scale/precision, then the unspecified scale/precision of the double divisor
     * should have no impact on the returned scale/precision.
     * @param divisor value by which this Score is to be divided
     * @return this / divisor
     */
    S divide(double divisor);

    /**
     * Returns an array of doubles representing the Score. Each double represents 1 score level.
     * A greater score level uses a lower array index than a lesser score level.
     * <p/>
     * When rounding is needed, each rounding should be floored (as defined by {@link Math#floor(double)}.
     * The length of the returned array must be stable for a specific {@link Score} implementation.
     * <p/>
     * For example: -0hard/-7soft returns new double{-0.0, -7.0}
     * @return never null
     */
    double[] toDoubleLevels();

}
