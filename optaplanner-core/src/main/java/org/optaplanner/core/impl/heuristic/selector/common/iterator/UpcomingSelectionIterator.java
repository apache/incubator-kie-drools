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

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.heuristic.selector.Selector;
import org.optaplanner.core.impl.heuristic.selector.entity.mimic.MimicReplayingEntitySelector;

/**
 * IMPORTANT: The constructor of any subclass of this abstract class, should never call any of its child
 * {@link Selector}'s {@link Iterator#hasNext()} or {@link Iterator#next()} methods,
 * because that can cause descendant {@link Selector}s to be selected too early
 * (which breaks {@link MimicReplayingEntitySelector}).
 *
 * @param <S> Selection type, for example a {@link Move} class, an entity class or a value class.
 */
public abstract class UpcomingSelectionIterator<S> extends SelectionIterator<S> {

    protected boolean upcomingCreated = false;
    protected boolean hasUpcomingSelection = true;
    protected S upcomingSelection;

    @Override
    public boolean hasNext() {
        if (!upcomingCreated) {
            upcomingSelection = createUpcomingSelection();
            upcomingCreated = true;
        }
        return hasUpcomingSelection;
    }

    @Override
    public S next() {
        if (!hasUpcomingSelection) {
            throw new NoSuchElementException();
        }
        if (!upcomingCreated) {
            upcomingSelection = createUpcomingSelection();
        }
        upcomingCreated = false;
        return upcomingSelection;
    }

    protected abstract S createUpcomingSelection();

    protected S noUpcomingSelection() {
        hasUpcomingSelection = false;
        return null;
    }

    @Override
    public String toString() {
        if (!upcomingCreated) {
            return "Next upcoming (?)";
        } else if (!hasUpcomingSelection) {
            return "No next upcoming";
        } else {
            return "Next upcoming (" + upcomingSelection + ")";
        }
    }

}
