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

package org.optaplanner.core.impl.move;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;

import org.optaplanner.core.config.localsearch.decider.acceptor.AcceptorConfig;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.impl.heuristic.selector.move.MoveSelector;
import org.optaplanner.core.impl.heuristic.selector.move.factory.MoveListFactory;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.core.impl.solution.Solution;

/**
 * A Move represents a change of 1 or more planning variables of 1 or more planning entities in the {@link Solution}.
 * <p/>
 * Usually the move holds a direct reference to each planning entity of the {@link Solution}
 * that it will change when {@link #doMove(ScoreDirector)} is called.
 * On that change it should also notify the {@link ScoreDirector} accordingly.
 * <p/>
 * A Move should implement {@link Object#equals(Object)} and {@link Object#hashCode()}.
 */
public interface Move {

    /**
     * Called before a move is evaluated to decide whether the move can be done and evaluated.
     * A Move is not doable if:
     * <ul>
     * <li>Either doing it would change nothing in the {@link Solution}.</li>
     * <li>Either it's simply not possible to do (for example due to build-in hard constraints).</li>
     * </ul>
     * <p/>
     * It is recommended to keep this method implementation simple: do not use it in an attempt to satisfy normal
     * hard and soft constraints.
     * <p/>
     * Although you could also filter out non-doable moves in for example the {@link MoveSelector}
     * or {@link MoveListFactory}, this is not needed as the {@link Solver} will do it for you.
     * @param scoreDirector the {@link ScoreDirector} not yet modified by the move.
     * @return true if the move achieves a change in the solution and the move is possible to do on the solution.
     */
    boolean isMoveDoable(ScoreDirector scoreDirector);

    /**
     * Called before the move is done, so the move can be evaluated and then be undone
     * without resulting into a permanent change in the solution.
     * @param scoreDirector the {@link ScoreDirector} not yet modified by the move.
     * @return an undoMove which does the exact opposite of this move.
     */
    Move createUndoMove(ScoreDirector scoreDirector);

    /**
     * Does the Move and updates the {@link Solution} and its {@link ScoreDirector} accordingly.
     * When the {@link Solution} is modified, the {@link ScoreDirector} should be correctly notified,
     * otherwise later calculated {@link Score}s can be corrupted.
     * @param scoreDirector never null, the {@link ScoreDirector} that needs to get notified of the changes.
     */
    void doMove(ScoreDirector scoreDirector);

    /**
     * Returns all planning entities that are being changed by this move.
     * Required for {@link AcceptorConfig.AcceptorType#PLANNING_ENTITY_TABU}.
     * <p/>
     * Duplicates entries in the returned {@link Collection} are best avoided.
     * The returned {@link Collection} is recommended to be in a stable order.
     * For example: use {@link List} or {@link LinkedHashSet}, but not {@link HashSet}.
     * @return never null
     */
    Collection<? extends Object> getPlanningEntities();

    /**
     * Returns all planning values that entities are being assigned to by this move.
     * Required for {@link AcceptorConfig.AcceptorType#PLANNING_VALUE_TABU}.
     * <p/>
     * Duplicates entries in the returned {@link Collection} are best avoided.
     * The returned {@link Collection} is recommended to be in a stable order.
     * For example: use {@link List} or {@link LinkedHashSet}, but not {@link HashSet}.
     * @return never null
     */
    Collection<? extends Object> getPlanningValues();

}
