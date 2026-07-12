/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.optaplanner.core.impl.heuristic.selector.move.decorator;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.heuristic.selector.common.iterator.SelectionIterator;
import org.optaplanner.core.impl.heuristic.selector.move.AbstractMoveSelector;
import org.optaplanner.core.impl.heuristic.selector.move.MoveSelector;

public class SelectedCountLimitMoveSelector<Solution_> extends AbstractMoveSelector<Solution_> {

    protected final MoveSelector<Solution_> childMoveSelector;
    protected final long selectedCountLimit;

    public SelectedCountLimitMoveSelector(MoveSelector<Solution_> childMoveSelector, long selectedCountLimit) {
        this.childMoveSelector = childMoveSelector;
        this.selectedCountLimit = selectedCountLimit;
        if (selectedCountLimit < 0L) {
            throw new IllegalArgumentException("The selector (" + this
                    + ") has a negative selectedCountLimit (" + selectedCountLimit + ").");
        }
        phaseLifecycleSupport.addEventListener(childMoveSelector);
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public boolean isCountable() {
        return true;
    }

    @Override
    public boolean isNeverEnding() {
        return false;
    }

    @Override
    public long getSize() {
        long childSize = childMoveSelector.getSize();
        return Math.min(selectedCountLimit, childSize);
    }

    @Override
    public Iterator<Move<Solution_>> iterator() {
        return new SelectedCountLimitMoveIterator(childMoveSelector.iterator());
    }

    private class SelectedCountLimitMoveIterator extends SelectionIterator<Move<Solution_>> {

        private final Iterator<Move<Solution_>> childMoveIterator;
        private long selectedSize;

        public SelectedCountLimitMoveIterator(Iterator<Move<Solution_>> childMoveIterator) {
            this.childMoveIterator = childMoveIterator;
            selectedSize = 0L;
        }

        @Override
        public boolean hasNext() {
            return selectedSize < selectedCountLimit && childMoveIterator.hasNext();
        }

        @Override
        public Move<Solution_> next() {
            if (selectedSize >= selectedCountLimit) {
                throw new NoSuchElementException();
            }
            selectedSize++;
            return childMoveIterator.next();
        }

    }

    @Override
    public String toString() {
        return "SelectedCountLimit(" + childMoveSelector + ")";
    }

}
