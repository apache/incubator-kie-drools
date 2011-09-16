/*
 * Copyright 2011 JBoss Inc
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

package org.drools.planner.core.solver;

import org.drools.FactHandle;
import org.drools.WorkingMemory;
import org.drools.planner.core.Solver;
import org.drools.planner.core.solution.Solution;
import org.drools.planner.core.solution.director.SolutionDirector;

/**
 * A ProblemFactChange represents a change in 1 or more problem facts of a {@link Solution}.
 * Problem facts used by a {@link Solver} must not be changed while it is solving,
 * but by scheduling this command to the {@link Solver}, you can change them when the time is right.
 * <p/>
 * Note that the {@link Solver} clones a {@link Solution} at will.
 * So any change must be done on the problem facts and planning entities referenced by the {@link Solution}
 * on the {@link SolutionDirector}. On each change it should also notify the {@link WorkingMemory} accordingly.
 */
public interface ProblemFactChange {

    /**
     * Does the change and updates the {@link Solution} and its {@link WorkingMemory} accordingly.
     * When the solution is modified, the {@link WorkingMemory}'s {@link FactHandle}s must be correctly notified,
     * otherwise the score(s) calculated will be corrupted.
     * @param solutionDirector never null.
     * Contains the working {@link Solution} which contains the planning facts (and planning entities) to change.
     * Also contains the {@link WorkingMemory} that needs to get notified of those changes.
     */
    void doChange(SolutionDirector solutionDirector);

}
