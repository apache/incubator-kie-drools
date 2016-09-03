/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.heuristic.selector.entity.decorator;

import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.heuristic.selector.common.iterator.SelectionIterator;
import org.optaplanner.core.impl.heuristic.selector.entity.AbstractEntitySelector;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;

public class SelectedCountLimitEntitySelector extends AbstractEntitySelector {

    protected final EntitySelector childEntitySelector;
    protected final boolean randomSelection;
    protected final long selectedCountLimit;

    public SelectedCountLimitEntitySelector(EntitySelector childEntitySelector, boolean randomSelection,
            long selectedCountLimit) {
        this.childEntitySelector = childEntitySelector;
        this.randomSelection = randomSelection;
        this.selectedCountLimit = selectedCountLimit;
        if (selectedCountLimit < 0L) {
            throw new IllegalArgumentException("The selector (" + this
                    + ") has a negative selectedCountLimit (" + selectedCountLimit + ").");
        }
        phaseLifecycleSupport.addEventListener(childEntitySelector);
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public EntityDescriptor getEntityDescriptor() {
        return childEntitySelector.getEntityDescriptor();
    }

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
        long childSize = childEntitySelector.getSize();
        return Math.min(selectedCountLimit, childSize);
    }

    @Override
    public Iterator<Object> iterator() {
        return new SelectedCountLimitEntityIterator(childEntitySelector.iterator());
    }

    @Override
    public ListIterator<Object> listIterator() {
        // TODO Not yet implemented
        throw new UnsupportedOperationException();
    }

    @Override
    public ListIterator<Object> listIterator(int index) {
        // TODO Not yet implemented
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<Object> endingIterator() {
        if (randomSelection) {
            // With random selection, the first n elements can differ between iterator calls,
            // so it's illegal to only return the first n elements in original order (that breaks NearbySelection)
            return childEntitySelector.endingIterator();
        } else {
            return new SelectedCountLimitEntityIterator(childEntitySelector.endingIterator());
        }
    }

    private class SelectedCountLimitEntityIterator extends SelectionIterator<Object> {

        private final Iterator<Object> childEntityIterator;
        private long selectedSize;

        public SelectedCountLimitEntityIterator(Iterator<Object> childEntityIterator) {
            this.childEntityIterator = childEntityIterator;
            selectedSize = 0L;
        }

        @Override
        public boolean hasNext() {
            return selectedSize < selectedCountLimit && childEntityIterator.hasNext();
        }

        @Override
        public Object next() {
            if (selectedSize >= selectedCountLimit) {
                throw new NoSuchElementException();
            }
            selectedSize++;
            return childEntityIterator.next();
        }

    }

    @Override
    public String toString() {
        return "SelectedCountLimit(" + childEntitySelector + ")";
    }

}
