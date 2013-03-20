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

package org.optaplanner.core.impl.heuristic.selector.move.composite;

import java.util.List;

import org.optaplanner.core.impl.heuristic.selector.move.AbstractMoveSelector;
import org.optaplanner.core.impl.heuristic.selector.move.MoveSelector;

/**
 * Abstract superclass for every composite {@link MoveSelector}.
 * @see MoveSelector
 */
public abstract class CompositeMoveSelector extends AbstractMoveSelector {

    protected final List<MoveSelector> childMoveSelectorList;
    protected final boolean randomSelection;

    protected CompositeMoveSelector(List<MoveSelector> childMoveSelectorList, boolean randomSelection) {
        this.childMoveSelectorList = childMoveSelectorList;
        this.randomSelection = randomSelection;
        for (MoveSelector childMoveSelector : childMoveSelectorList) {
            solverPhaseLifecycleSupport.addEventListener(childMoveSelector);
        }
        if (!randomSelection) {
            // Only the last childMoveSelector can be neverEnding
            if (!childMoveSelectorList.isEmpty()) {
                for (MoveSelector childMoveSelector
                        : childMoveSelectorList.subList(0, childMoveSelectorList.size() - 1)) {
                    if (childMoveSelector.isNeverEnding()) {
                        throw new IllegalStateException("The selector (" + this
                                + ")'s non-last childMoveSelector (" + childMoveSelector
                                + ") has neverEnding (" + childMoveSelector.isNeverEnding()
                                + ") with randomSelection (" + randomSelection + ").");
                    }
                }
            }
        }
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public boolean isContinuous() {
        for (MoveSelector moveSelector : childMoveSelectorList) {
            if (moveSelector.isContinuous()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + childMoveSelectorList + ")";
    }

}
