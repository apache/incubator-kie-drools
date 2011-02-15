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

package org.drools.planner.core.score;

/**
 * A Score is result of the score function (AKA fitness function) on a single possible solution.
 * <p/>
 * Implementations must be immutable.
 * @see AbstractScore
 * @see DefaultHardAndSoftScore
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
     * @param multiplicand value to be multiplied by this Score.
     * @return this * multiplicand
     */
    S multiply(double  multiplicand);

    /**
     * Returns a Score whose value is (this / divisor).
     * When rounding is needed, it should be floored (as defined by {@link Math#floor(double)}.
     * @param divisor value by which this Score is to be divided
     * @return this / divisor
     */
    S divide(double divisor);

}
