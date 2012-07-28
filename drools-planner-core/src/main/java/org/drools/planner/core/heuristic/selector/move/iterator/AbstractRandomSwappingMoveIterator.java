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

package org.drools.planner.core.heuristic.selector.move.iterator;

import java.util.Iterator;

import org.drools.planner.core.heuristic.selector.common.iterator.UpcomingSelectionIterator;
import org.drools.planner.core.move.Move;

public abstract class AbstractRandomSwappingMoveIterator<SubSelection> extends UpcomingSelectionIterator<Move> {

    protected final Iterable<SubSelection> leftSubSelector;
    protected final Iterable<SubSelection> rightSubSelector;

    protected Iterator<SubSelection> leftSubSelectionIterator;
    protected Iterator<SubSelection> rightSubSelectionIterator;

    protected AbstractRandomSwappingMoveIterator(Iterable<SubSelection> leftSubSelector,
            Iterable<SubSelection> rightSubSelector) {
        this.leftSubSelector = leftSubSelector;
        this.rightSubSelector = rightSubSelector;
        leftSubSelectionIterator = this.leftSubSelector.iterator();
        rightSubSelectionIterator = this.rightSubSelector.iterator();
        if (!leftSubSelectionIterator.hasNext() || !rightSubSelectionIterator.hasNext()) {
            upcomingSelection = null;
        } else {
            createUpcomingSelection();
        }
    }

    @Override
    protected void createUpcomingSelection() {
        // Ideally, this code should have read:
        //     SubSelection leftSubSelection = leftSubSelectionIterator.next();
        //     SubSelection rightSubSelection = rightSubSelectionIterator.next();
        // But empty selectors and ending selectors (such as non-random or shuffled) make it more complex
        if (!leftSubSelectionIterator.hasNext()) {
            leftSubSelectionIterator = leftSubSelector.iterator();
        }
        SubSelection leftSubSelection = leftSubSelectionIterator.next();
        if (!rightSubSelectionIterator.hasNext()) {
            rightSubSelectionIterator = rightSubSelector.iterator();
        }
        SubSelection rightSubSelection = rightSubSelectionIterator.next();
        upcomingSelection = newSwappingMove(leftSubSelection, rightSubSelection);
    }

    protected abstract Move newSwappingMove(SubSelection leftSubSelection, SubSelection rightSubSelection);

}
