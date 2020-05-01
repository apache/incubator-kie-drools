/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

import java.util.Collections;
import java.util.ListIterator;

import org.optaplanner.core.impl.heuristic.selector.ListIterableSelector;

public abstract class AbstractOriginalSwapIterator<S, SubS> extends UpcomingSelectionIterator<S> {

    public static <SubS> long getSize(ListIterableSelector leftSubSelector, ListIterableSelector rightSubSelector) {
        if (leftSubSelector != rightSubSelector) {
            return leftSubSelector.getSize() * rightSubSelector.getSize();
        } else {
            long leftSize = leftSubSelector.getSize();
            return leftSize * (leftSize - 1L) / 2L;
        }
    }

    protected final ListIterable<SubS> leftSubSelector;
    protected final ListIterable<SubS> rightSubSelector;
    protected final boolean leftEqualsRight;

    private ListIterator<SubS> leftSubSelectionIterator;
    private ListIterator<SubS> rightSubSelectionIterator;

    private SubS leftSubSelection;

    public AbstractOriginalSwapIterator(ListIterable<SubS> leftSubSelector,
            ListIterable<SubS> rightSubSelector) {
        this.leftSubSelector = leftSubSelector;
        this.rightSubSelector = rightSubSelector;
        leftEqualsRight = (leftSubSelector == rightSubSelector);
        leftSubSelectionIterator = leftSubSelector.listIterator();
        rightSubSelectionIterator = Collections.<SubS> emptyList().listIterator();
        // Don't do hasNext() in constructor (to avoid upcoming selections breaking mimic recording)
    }

    @Override
    protected S createUpcomingSelection() {
        if (!rightSubSelectionIterator.hasNext()) {
            if (!leftSubSelectionIterator.hasNext()) {
                return noUpcomingSelection();
            }
            leftSubSelection = leftSubSelectionIterator.next();

            if (!leftEqualsRight) {
                rightSubSelectionIterator = rightSubSelector.listIterator();
                if (!rightSubSelectionIterator.hasNext()) {
                    return noUpcomingSelection();
                }
            } else {
                // Select A-B, A-C, B-C. Do not select B-A, C-A, C-B. Do not select A-A, B-B, C-C.
                if (!leftSubSelectionIterator.hasNext()) {
                    return noUpcomingSelection();
                }
                rightSubSelectionIterator = rightSubSelector.listIterator(leftSubSelectionIterator.nextIndex());
                // rightEntityIterator's first hasNext() always returns true because of the nextIndex()
            }
        }
        SubS rightSubSelection = rightSubSelectionIterator.next();
        return newSwapSelection(leftSubSelection, rightSubSelection);
    }

    protected abstract S newSwapSelection(SubS leftSubSelection, SubS rightSubSelection);

}
