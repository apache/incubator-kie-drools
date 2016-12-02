/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.api.domain.solution.cloner;

import org.optaplanner.core.api.domain.solution.PlanningSolution;

/**
 * Clones a {@link PlanningSolution} during planning.
 * Used to remember the state of a good {@link PlanningSolution} so it can be recalled at a later time
 * when the original {@link PlanningSolution} is already modified.
 * Also used in population based heuristics to increase or repopulate the population.
 * <p>
 * Planning cloning is hard: avoid doing it yourself.
 * <p>
 * An implementing class must be thread-safe after initialization.
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
public interface SolutionCloner<Solution_> {

    /**
     * Does a planning clone. The returned {@link PlanningSolution} clone must fulfill these requirements:
     * <ul>
     * <li>The clone must represent the same planning problem.
     * Usually it reuses the same instances of the problem facts and problem fact collections as the {@code original}.
     * </li>
     * <li>The clone must have the same (equal) score as the {@code original}.
     * </li>
     * <li>The clone must use different, cloned instances of the entities and entity collections.
     * If a cloned entity changes, the original must remain unchanged.
     * If an entity is added or removed in a cloned {@link PlanningSolution},
     * the original {@link PlanningSolution} must remain unchanged.</li>
     * </ul>
     * Note that a class might support more than 1 clone method: planning clone is just one of them.
     * <p>
     * This method is thread-safe.
     * @param original never null, the original {@link PlanningSolution}
     * @return never null, the cloned {@link PlanningSolution}
     */
     Solution_ cloneSolution(Solution_ original);

}
