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

public class MimicRecordingEntitySelector<Solution_> extends AbstractEntitySelector<Solution_>
        implements EntityMimicRecorder<Solution_> {

    protected final EntitySelector<Solution_> childEntitySelector;

    protected final List<MimicReplayingEntitySelector<Solution_>> replayingEntitySelectorList;

    public MimicRecordingEntitySelector(EntitySelector<Solution_> childEntitySelector) {
        this.childEntitySelector = childEntitySelector;
        phaseLifecycleSupport.addEventListener(childEntitySelector);
        replayingEntitySelectorList = new ArrayList<>();
    }

    @Override
    public void addMimicReplayingEntitySelector(MimicReplayingEntitySelector<Solution_> replayingEntitySelector) {
        replayingEntitySelectorList.add(replayingEntitySelector);
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public EntityDescriptor<Solution_> getEntityDescriptor() {
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
            for (MimicReplayingEntitySelector<Solution_> replayingEntitySelector : replayingEntitySelectorList) {
                replayingEntitySelector.recordedHasNext(hasNext);
            }
            return hasNext;
        }

        @Override
        public Object next() {
            Object next = childEntityIterator.next();
            for (MimicReplayingEntitySelector<Solution_> replayingEntitySelector : replayingEntitySelectorList) {
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
            for (MimicReplayingEntitySelector<Solution_> replayingEntitySelector : replayingEntitySelectorList) {
                replayingEntitySelector.recordedHasNext(hasNext);
            }
            return hasNext;
        }

        @Override
        public Object next() {
            Object next = childEntityIterator.next();
            for (MimicReplayingEntitySelector<Solution_> replayingEntitySelector : replayingEntitySelectorList) {
                replayingEntitySelector.recordedNext(next);
            }
            return next;
        }

        @Override
        public boolean hasPrevious() {
            boolean hasPrevious = childEntityIterator.hasPrevious();
            for (MimicReplayingEntitySelector<Solution_> replayingEntitySelector : replayingEntitySelectorList) {
                // The replay only cares that the recording changed, not in which direction
                replayingEntitySelector.recordedHasNext(hasPrevious);
            }
            return hasPrevious;
        }

        @Override
        public Object previous() {
            Object previous = childEntityIterator.previous();
            for (MimicReplayingEntitySelector<Solution_> replayingEntitySelector : replayingEntitySelectorList) {
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
