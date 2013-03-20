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

package org.optaplanner.core.config.solver;

import java.util.Random;

import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.impl.localsearch.decider.DefaultDecider;
import org.optaplanner.core.impl.move.Move;

/**
 * The environment mode also allows you to detect common bugs in your implementation.
 * <p/>
 * Also, a {@link Solver} has a single {@link Random} instance.
 * Some optimization algorithms use the {@link Random} instance a lot more than others.
 * For example simulated annealing depends highly on random numbers,
 * while tabu search only depends on it to deal with score ties.
 * This environment mode influences the seed of that {@link Random} instance.
 */
public enum EnvironmentMode {
    /**
     * This mode does a few more assertions (such as {@link DefaultDecider#assertMoveScoreIsUncorrupted})
     * than the {@link #FAST_ASSERT} mode at a horrible performance cost.
     * <p/>
     * This mode is reproducible (see {@link #REPRODUCIBLE} mode).
     * <p/>
     * This mode is horribly slow.
     */
    FULL_ASSERT,
    /**
     * This mode turns on most assertions (such as {@link DefaultDecider#assertUndoMoveIsUncorrupted})
     * to fail-fast on a bug in a {@link Move} implementation, in a score rule or something else.
     * <p/>
     * This mode is reproducible (see {@link #REPRODUCIBLE} mode).
     * <p/>
     * This mode is slow.
     */
    FAST_ASSERT,
    /**
     * The reproducible mode is the default mode because it is recommended during development.
     * In this mode, 2 runs on the same computer will execute the same code in the same order.
     * They will also yield the same result, except if they use a time based termination
     * and they have a sufficiently large difference in allocated CPU time.
     * This allows you to benchmark new optimizations (such as a new Move implementation) fairly.
     * <p>
     * The reproducible mode is not much slower than the production mode.
     * </p>
     * In practice, this mode uses the default random seed,
     * and it also disables certain concurrency optimizations (such as work stealing).
     * TODO: JBRULES-681 Multi-threaded support which implement those concurrency optimizations
     */
    REPRODUCIBLE,
    /**
     * The production mode is the fastest and the most robust, but not reproducible.
     * It is recommended for a production environment.
     * <p/>
     * The random seed is different on every run, which makes it more robust against an unlucky random seed.
     * An unlucky random seed gives a bad result on a certain data set with a certain solver configuration.
     * Note that in most use cases the impact of the random seed is relatively low on the result.
     * An occasional bad result is far more likely to be caused by another issue (such as a score trap).
     */
    PRODUCTION;
}
