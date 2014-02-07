/*
 * Copyright 2014 JBoss Inc
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

package org.optaplanner.core.impl.heuristic.selector.move.decorator;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionFilter;
import org.optaplanner.core.impl.heuristic.selector.common.iterator.SelectionIterator;
import org.optaplanner.core.impl.heuristic.selector.common.iterator.UpcomingSelectionIterator;
import org.optaplanner.core.impl.heuristic.selector.move.AbstractMoveSelector;
import org.optaplanner.core.impl.heuristic.selector.move.MoveSelector;
import org.optaplanner.core.impl.move.Move;
import org.optaplanner.core.impl.score.director.ScoreDirector;

public class SelectedSizeLimitMoveSelector extends AbstractMoveSelector {

    protected final MoveSelector childMoveSelector;
    protected final long selectedSizeLimit;

    public SelectedSizeLimitMoveSelector(MoveSelector childMoveSelector, long selectedSizeLimit) {
        this.childMoveSelector = childMoveSelector;
        this.selectedSizeLimit = selectedSizeLimit;
        if (selectedSizeLimit < 0L) {
            throw new IllegalArgumentException("The selector (" + this
                    + ") has a negative selectedSizeLimit (" + selectedSizeLimit + ").");
        }
        solverPhaseLifecycleSupport.addEventListener(childMoveSelector);
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public boolean isCountable() {
        return true;
    }

    public boolean isNeverEnding() {
        return false;
    }

    public long getSize() {
        long childSize = childMoveSelector.getSize();
        return Math.min(selectedSizeLimit, childSize);
    }

    public Iterator<Move> iterator() {
        return new SelectedSizeLimitMoveIterator(childMoveSelector.iterator());
    }

    private class SelectedSizeLimitMoveIterator extends SelectionIterator<Move> {

        private final Iterator<Move> childMoveIterator;
        private long selectedSize;

        public SelectedSizeLimitMoveIterator(Iterator<Move> childMoveIterator) {
            this.childMoveIterator = childMoveIterator;
            selectedSize = 0L;
        }

        @Override
        public boolean hasNext() {
            return selectedSize < selectedSizeLimit && childMoveIterator.hasNext();
        }

        @Override
        public Move next() {
            if (selectedSize >= selectedSizeLimit) {
                throw new NoSuchElementException();
            }
            selectedSize++;
            return childMoveIterator.next();
        }

    }

    @Override
    public String toString() {
        return "SelectedSizeLimit(" + childMoveSelector + ")";
    }

}
