/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.heuristic.selector.value.mimic;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.heuristic.selector.common.iterator.SelectionIterator;
import org.optaplanner.core.impl.heuristic.selector.common.iterator.SelectionListIterator;
import org.optaplanner.core.impl.heuristic.selector.entity.mimic.MimicReplayingEntitySelector;
import org.optaplanner.core.impl.heuristic.selector.value.AbstractValueSelector;
import org.optaplanner.core.impl.heuristic.selector.value.EntityIndependentValueSelector;

public class MimicRecordingValueSelector extends AbstractValueSelector
        implements ValueMimicRecorder, EntityIndependentValueSelector {

    protected final EntityIndependentValueSelector childValueSelector;

    protected final List<MimicReplayingValueSelector> replayingValueSelectorList;

    public MimicRecordingValueSelector(EntityIndependentValueSelector childValueSelector) {
        this.childValueSelector = childValueSelector;
        phaseLifecycleSupport.addEventListener(childValueSelector);
        replayingValueSelectorList = new ArrayList<MimicReplayingValueSelector>();
    }

    @Override
    public void addMimicReplayingValueSelector(MimicReplayingValueSelector replayingEntitySelector) {
        replayingValueSelectorList.add(replayingEntitySelector);
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public GenuineVariableDescriptor getVariableDescriptor() {
        return childValueSelector.getVariableDescriptor();
    }

    public boolean isCountable() {
        return childValueSelector.isCountable();
    }

    public boolean isNeverEnding() {
        return childValueSelector.isNeverEnding();
    }

    public long getSize(Object entity) {
        return childValueSelector.getSize(entity);
    }

    public long getSize() {
        return childValueSelector.getSize();
    }

    public Iterator<Object> iterator(Object entity) {
        return new RecordingValueIterator(childValueSelector.iterator(entity));
    }

    public Iterator<Object> iterator() {
        return new RecordingValueIterator(childValueSelector.iterator());
    }

    private class RecordingValueIterator extends SelectionIterator<Object> {

        private final Iterator<Object> childValueIterator;

        public RecordingValueIterator(Iterator<Object> childValueIterator) {
            this.childValueIterator = childValueIterator;
        }

        public boolean hasNext() {
            boolean hasNext = childValueIterator.hasNext();
            for (MimicReplayingValueSelector replayingValueSelector : replayingValueSelectorList) {
                replayingValueSelector.recordedHasNext(hasNext);
            }
            return hasNext;
        }

        public Object next() {
            Object next = childValueIterator.next();
            for (MimicReplayingValueSelector replayingValueSelector : replayingValueSelectorList) {
                replayingValueSelector.recordedNext(next);
            }
            return next;
        }

    }

    public Iterator<Object> endingIterator(Object entity) {
        // No recording, because the endingIterator() is used for determining size
        return childValueSelector.endingIterator(entity);
    }

    @Override
    public String toString() {
        return "Recording(" + childValueSelector + ")";
    }

}
