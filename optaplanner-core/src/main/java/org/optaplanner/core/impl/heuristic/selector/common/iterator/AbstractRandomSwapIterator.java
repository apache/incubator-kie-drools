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

package org.optaplanner.core.impl.heuristic.selector.common.iterator;

import java.util.Iterator;

public abstract class AbstractRandomSwapIterator<S, SubS> extends UpcomingSelectionIterator<S> {

    protected final Iterable<SubS> leftSubSelector;
    protected final Iterable<SubS> rightSubSelector;

    protected Iterator<SubS> leftSubSelectionIterator;
    protected Iterator<SubS> rightSubSelectionIterator;

    public AbstractRandomSwapIterator(Iterable<SubS> leftSubSelector,
            Iterable<SubS> rightSubSelector) {
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
        //     SubS leftSubSelection = leftSubSelectionIterator.next();
        //     SubS rightSubSelection = rightSubSelectionIterator.next();
        // But empty selectors and ending selectors (such as non-random or shuffled) make it more complex
        if (!leftSubSelectionIterator.hasNext()) {
            leftSubSelectionIterator = leftSubSelector.iterator();
        }
        SubS leftSubSelection = leftSubSelectionIterator.next();
        if (!rightSubSelectionIterator.hasNext()) {
            rightSubSelectionIterator = rightSubSelector.iterator();
        }
        SubS rightSubSelection = rightSubSelectionIterator.next();
        upcomingSelection = newSwapSelection(leftSubSelection, rightSubSelection);
    }

    protected abstract S newSwapSelection(SubS leftSubSelection, SubS rightSubSelection);

}
