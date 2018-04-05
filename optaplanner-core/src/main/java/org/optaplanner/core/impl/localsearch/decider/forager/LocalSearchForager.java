/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.localsearch.decider.forager;

import org.optaplanner.core.impl.heuristic.selector.move.MoveSelector;
import org.optaplanner.core.impl.localsearch.decider.LocalSearchDecider;
import org.optaplanner.core.impl.localsearch.event.LocalSearchPhaseLifecycleListener;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchMoveScope;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchStepScope;

/**
 * Collects the moves and picks the next step from those for the {@link LocalSearchDecider}.
 * @see AbstractLocalSearchForager
 */
public interface LocalSearchForager extends LocalSearchPhaseLifecycleListener {

    /**
     * @return true if it can be combined with a {@link MoveSelector#isNeverEnding()} that returns true.
     */
    boolean supportsNeverEndingMoveSelector();

    /**
     * @param moveScope never null
     */
    void addMove(LocalSearchMoveScope moveScope);

    /**
     * @return true if no further moves should be selected (and evaluated) for this step.
     */
    boolean isQuitEarly();

    /**
     * @param stepScope never null
     * @return sometimes null, for example if no move is selected
     */
    LocalSearchMoveScope pickMove(LocalSearchStepScope stepScope);

}
