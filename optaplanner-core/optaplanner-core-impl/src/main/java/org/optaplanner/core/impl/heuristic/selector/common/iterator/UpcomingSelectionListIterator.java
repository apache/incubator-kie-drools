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

import java.util.ListIterator;
import java.util.NoSuchElementException;

public abstract class UpcomingSelectionListIterator<S> extends UpcomingSelectionIterator<S>
        implements ListIterator<S> {

    private int nextListIteratorIndex = 0;

    protected S previousSelection;
    protected boolean previousCreated = false;
    protected boolean hasPreviousSelection = false;

    protected S noPreviousSelection() {
        hasPreviousSelection = false;
        return null;
    }

    protected abstract S createUpcomingSelection();

    protected abstract S createPreviousSelection();

    @Override
    public boolean hasPrevious() {

        if (!previousCreated) {
            previousSelection = createPreviousSelection();
            previousCreated = true;
        }
        return hasPreviousSelection;
    }

    @Override
    public S next() {
        S next = super.next();
        nextListIteratorIndex++;
        hasPreviousSelection = true;
        return next;
    }

    @Override
    public S previous() {
        if (!hasPreviousSelection) {
            throw new NoSuchElementException();
        }
        if (!previousCreated) {
            previousSelection = createPreviousSelection();
        }
        previousCreated = false;
        nextListIteratorIndex--;
        hasUpcomingSelection = true;
        return previousSelection;
    }

    @Override
    public int nextIndex() {
        return nextListIteratorIndex;
    }

    @Override
    public int previousIndex() {
        return nextListIteratorIndex - 1;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("The optional operation remove() is not supported.");
    }

    @Override
    public void set(S o) {
        throw new UnsupportedOperationException("The optional operation set(...) is not supported.");
    }

    @Override
    public void add(S o) {
        throw new UnsupportedOperationException("The optional operation add(...) is not supported.");
    }
}
