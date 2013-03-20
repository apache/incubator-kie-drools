/*
 * Copyright 2012 JBoss Inc
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

import org.optaplanner.core.impl.solution.Solution;

/**
 * Tagging interface for a {@link Solution} that implements its own planning cloning
 * instead of letting the default or a custom {@link SolutionCloner} do it.
 * <p/>
 * Planning cloning is hard: avoid doing it yourself.
 */
public interface PlanningCloneable<T> {

    /**
     * Does a planning clone. A returned {@link Solution} clone must fulfill these requirements:
     * <ul>
     * <li>The clone must represent the same planning problem.
     * Usually it reuses the same instances of the problem facts and problem fact collections as the {@code original}.
     * </li>
     * <li>The clone must use different, cloned instances of the entities and entity collections.
     * If a cloned entity changes, the original must remain unchanged.
     * If an entity is added or removed in a cloned {@link Solution},
     * the original {@link Solution} must remain unchanged.</li>
     * </ul>
     * Note that a class might support more than 1 clone method: planning clone is just one of them.
     * @return never null, the cloned {@link Solution}
     */
    T planningClone();

}
