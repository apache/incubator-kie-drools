/**
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

package org.drools.planner.core.move;

import org.drools.FactHandle;
import org.drools.WorkingMemory;
import org.drools.planner.core.Solver;
import org.drools.planner.core.move.factory.MoveFactory;
import org.drools.planner.core.solution.Solution;

/**
 * A Move represents a change in the solution.
 * </p>
 * Ussually the move holds a direct pointer to the part of the solution
 * that it will change when {@link #doMove(WorkingMemory)} is called.
 * On that change it should also notify the {@link WorkingMemory} accordingly.
 * </p>
 * A Move should implement {@link Object#equals(Object)} and {@link Object#hashCode()}.
 * @author Geoffrey De Smet
 */
public interface Move {

    /**
     * Called before a move is evaluated to decide wheter the move can be done and evaluated.
     * A Move isn't doable if:
     * <ul>
     * <li>Either doing it would change nothing in the solution.</li>
     * <li>Either it's simply not possible to do.</li>
     * </ul>
     * Although you could filter out non-doable moves in for example the {@link MoveFactory},
     * this is not needed as the {@link Solver} will do it for you.
     * @param workingMemory the {@link WorkingMemory} not yet modified by the move.
     * @return true if the move achieves a change in the solution and the move is possible to do on the solution.
     */
    boolean isMoveDoable(WorkingMemory workingMemory);

    /**
     * Called before the move is done, so the move can be evaluated and then be undone
     * without resulting into a permanent change in the solution.
     * @param workingMemory the {@link WorkingMemory} not yet modified by the move.
     * @return an undoMove which does the exact opposite of this move.
     */
    Move createUndoMove(WorkingMemory workingMemory);

    /**
     * Does the Move and updates the {@link Solution} and its {@link WorkingMemory} accordingly.
     * When the solution is modified, the {@link WorkingMemory}'s {@link FactHandle}s should be correctly notified,
     * otherwise the score(s) calculated will be corrupted.
     * @param workingMemory the {@link WorkingMemory} that needs to get notified of the changes.
     */
    void doMove(WorkingMemory workingMemory);

}
