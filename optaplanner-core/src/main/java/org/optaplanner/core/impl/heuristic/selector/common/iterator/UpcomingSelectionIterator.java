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

import java.util.NoSuchElementException;

public abstract class UpcomingSelectionIterator<S> extends SelectionIterator<S>  {

    protected boolean upcomingCreated = false;
    protected boolean hasUpcomingSelection = true;
    protected S upcomingSelection;

    public boolean hasNext() {
        if (!upcomingCreated) {
            upcomingSelection = createUpcomingSelection();
            upcomingCreated = true;
        }
        return hasUpcomingSelection;
    }

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

}
