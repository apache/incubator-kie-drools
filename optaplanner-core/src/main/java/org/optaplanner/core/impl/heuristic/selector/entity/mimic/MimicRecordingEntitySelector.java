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

package org.optaplanner.core.impl.heuristic.selector.entity.mimic;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.heuristic.selector.common.iterator.SelectionIterator;
import org.optaplanner.core.impl.heuristic.selector.common.iterator.SelectionListIterator;
import org.optaplanner.core.impl.heuristic.selector.entity.AbstractEntitySelector;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;

public class MimicRecordingEntitySelector extends AbstractEntitySelector implements EntityMimicRecorder {

    protected final EntitySelector childEntitySelector;

    protected final List<MimicReplayingEntitySelector> replayingEntitySelectorList;

    public MimicRecordingEntitySelector(EntitySelector childEntitySelector) {
        this.childEntitySelector = childEntitySelector;
        phaseLifecycleSupport.addEventListener(childEntitySelector);
        replayingEntitySelectorList = new ArrayList<>();
    }

    @Override
    public void addMimicReplayingEntitySelector(MimicReplayingEntitySelector replayingEntitySelector) {
        replayingEntitySelectorList.add(replayingEntitySelector);
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
        return childEntitySelector.isCountable();
    }

    @Override
    public boolean isNeverEnding() {
        return childEntitySelector.isNeverEnding();
    }

    @Override
    public long getSize() {
        return childEntitySelector.getSize();
    }

    @Override
    public Iterator<Object> iterator() {
        return new RecordingEntityIterator(childEntitySelector.iterator());
    }

    private class RecordingEntityIterator extends SelectionIterator<Object> {

        private final Iterator<Object> childEntityIterator;

        public RecordingEntityIterator(Iterator<Object> childEntityIterator) {
            this.childEntityIterator = childEntityIterator;
        }

        @Override
        public boolean hasNext() {
            boolean hasNext = childEntityIterator.hasNext();
            for (MimicReplayingEntitySelector replayingEntitySelector : replayingEntitySelectorList) {
                replayingEntitySelector.recordedHasNext(hasNext);
            }
            return hasNext;
        }

        @Override
        public Object next() {
            Object next = childEntityIterator.next();
            for (MimicReplayingEntitySelector replayingEntitySelector : replayingEntitySelectorList) {
                replayingEntitySelector.recordedNext(next);
            }
            return next;
        }

    }

    @Override
    public Iterator<Object> endingIterator() {
        // No recording, because the endingIterator() is used for determining size
        return childEntitySelector.endingIterator();
    }

    @Override
    public ListIterator<Object> listIterator() {
        return new RecordingEntityListIterator(childEntitySelector.listIterator());
    }

    @Override
    public ListIterator<Object> listIterator(int index) {
        return new RecordingEntityListIterator(childEntitySelector.listIterator(index));
    }

    private class RecordingEntityListIterator extends SelectionListIterator<Object> {

        private final ListIterator<Object> childEntityIterator;

        public RecordingEntityListIterator(ListIterator<Object> childEntityIterator) {
            this.childEntityIterator = childEntityIterator;
        }

        @Override
        public boolean hasNext() {
            boolean hasNext = childEntityIterator.hasNext();
            for (MimicReplayingEntitySelector replayingEntitySelector : replayingEntitySelectorList) {
                replayingEntitySelector.recordedHasNext(hasNext);
            }
            return hasNext;
        }

        @Override
        public Object next() {
            Object next = childEntityIterator.next();
            for (MimicReplayingEntitySelector replayingEntitySelector : replayingEntitySelectorList) {
                replayingEntitySelector.recordedNext(next);
            }
            return next;
        }

        @Override
        public boolean hasPrevious() {
            boolean hasPrevious = childEntityIterator.hasPrevious();
            for (MimicReplayingEntitySelector replayingEntitySelector : replayingEntitySelectorList) {
                // The replay only cares that the recording changed, not in which direction
                replayingEntitySelector.recordedHasNext(hasPrevious);
            }
            return hasPrevious;
        }

        @Override
        public Object previous() {
            Object previous = childEntityIterator.previous();
            for (MimicReplayingEntitySelector replayingEntitySelector : replayingEntitySelectorList) {
                // The replay only cares that the recording changed, not in which direction
                replayingEntitySelector.recordedNext(previous);
            }
            return previous;
        }

        @Override
        public int nextIndex() {
            return childEntityIterator.nextIndex();
        }

        @Override
        public int previousIndex() {
            return childEntityIterator.previousIndex();
        }

    }

    @Override
    public String toString() {
        return "Recording(" + childEntitySelector + ")";
    }

}
