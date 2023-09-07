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

package org.optaplanner.core.impl.heuristic.selector.value.mimic;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.heuristic.selector.AbstractDemandEnabledSelector;
import org.optaplanner.core.impl.heuristic.selector.common.iterator.SelectionIterator;
import org.optaplanner.core.impl.heuristic.selector.value.EntityIndependentValueSelector;

public class MimicRecordingValueSelector<Solution_>
        extends AbstractDemandEnabledSelector<Solution_>
        implements ValueMimicRecorder<Solution_>, EntityIndependentValueSelector<Solution_> {

    protected final EntityIndependentValueSelector<Solution_> childValueSelector;

    protected final List<MimicReplayingValueSelector<Solution_>> replayingValueSelectorList;

    public MimicRecordingValueSelector(EntityIndependentValueSelector<Solution_> childValueSelector) {
        this.childValueSelector = childValueSelector;
        phaseLifecycleSupport.addEventListener(childValueSelector);
        replayingValueSelectorList = new ArrayList<>();
    }

    @Override
    public void addMimicReplayingValueSelector(MimicReplayingValueSelector<Solution_> replayingValueSelector) {
        replayingValueSelectorList.add(replayingValueSelector);
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public GenuineVariableDescriptor<Solution_> getVariableDescriptor() {
        return childValueSelector.getVariableDescriptor();
    }

    @Override
    public boolean isCountable() {
        return childValueSelector.isCountable();
    }

    @Override
    public boolean isNeverEnding() {
        return childValueSelector.isNeverEnding();
    }

    @Override
    public long getSize(Object entity) {
        return childValueSelector.getSize(entity);
    }

    @Override
    public long getSize() {
        return childValueSelector.getSize();
    }

    @Override
    public Iterator<Object> iterator(Object entity) {
        return new RecordingValueIterator(childValueSelector.iterator(entity));
    }

    @Override
    public Iterator<Object> iterator() {
        return new RecordingValueIterator(childValueSelector.iterator());
    }

    private class RecordingValueIterator extends SelectionIterator<Object> {

        private final Iterator<Object> childValueIterator;

        public RecordingValueIterator(Iterator<Object> childValueIterator) {
            this.childValueIterator = childValueIterator;
        }

        @Override
        public boolean hasNext() {
            boolean hasNext = childValueIterator.hasNext();
            for (MimicReplayingValueSelector<Solution_> replayingValueSelector : replayingValueSelectorList) {
                replayingValueSelector.recordedHasNext(hasNext);
            }
            return hasNext;
        }

        @Override
        public Object next() {
            Object next = childValueIterator.next();
            for (MimicReplayingValueSelector<Solution_> replayingValueSelector : replayingValueSelectorList) {
                replayingValueSelector.recordedNext(next);
            }
            return next;
        }

    }

    @Override
    public Iterator<Object> endingIterator(Object entity) {
        // No recording, because the endingIterator() is used for determining size
        return childValueSelector.endingIterator(entity);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;
        if (other == null || getClass() != other.getClass())
            return false;
        MimicRecordingValueSelector<?> that = (MimicRecordingValueSelector<?>) other;
        /*
         * Using list size in order to prevent recursion in equals/hashcode.
         * Since the replaying selector will always point back to this instance,
         * we only need to know if the lists are the same
         * in order to be able to tell if two instances are equal.
         */
        return Objects.equals(childValueSelector, that.childValueSelector)
                && Objects.equals(replayingValueSelectorList.size(), that.replayingValueSelectorList.size());
    }

    @Override
    public int hashCode() {
        return Objects.hash(childValueSelector, replayingValueSelectorList.size());
    }

    @Override
    public String toString() {
        return "Recording(" + childValueSelector + ")";
    }

}
