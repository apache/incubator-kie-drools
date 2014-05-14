package org.optaplanner.core.impl.heuristic.selector.entity.mimic;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.heuristic.selector.common.iterator.SelectionIterator;
import org.optaplanner.core.impl.heuristic.selector.entity.AbstractEntitySelector;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;

public class MimicRecordingEntitySelector extends AbstractEntitySelector implements EntityMimicRecorder {

    protected final EntitySelector childEntitySelector;

    protected final List<MimicReplayingEntitySelector> replayingEntitySelectorList;

    public MimicRecordingEntitySelector(EntitySelector childEntitySelector) {
        this.childEntitySelector = childEntitySelector;
        phaseLifecycleSupport.addEventListener(childEntitySelector);
        replayingEntitySelectorList = new ArrayList<MimicReplayingEntitySelector>();
    }

    @Override
    public void addMimicReplayingEntitySelector(MimicReplayingEntitySelector replayingEntitySelector) {
        replayingEntitySelectorList.add(replayingEntitySelector);
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public EntityDescriptor getEntityDescriptor() {
        return childEntitySelector.getEntityDescriptor();
    }

    public boolean isCountable() {
        return childEntitySelector.isCountable();
    }

    public boolean isNeverEnding() {
        return childEntitySelector.isNeverEnding();
    }

    public long getSize() {
        return childEntitySelector.getSize();
    }

    public Iterator<Object> iterator() {
        return new RecordingEntityIterator(childEntitySelector.iterator());
    }

    private class RecordingEntityIterator extends SelectionIterator<Object> {

        private final Iterator<Object> childEntityIterator;

        public RecordingEntityIterator(Iterator<Object> childEntityIterator) {
            this.childEntityIterator = childEntityIterator;
        }

        public boolean hasNext() {
            boolean hasNext = childEntityIterator.hasNext();
            for (MimicReplayingEntitySelector replayingEntitySelector : replayingEntitySelectorList) {
                replayingEntitySelector.recordedHasNext(hasNext);
            }
            return hasNext;
        }

        public Object next() {
            Object next = childEntityIterator.next();
            for (MimicReplayingEntitySelector replayingEntitySelector : replayingEntitySelectorList) {
                replayingEntitySelector.recordedNext(next);
            }
            return next;
        }

    }

    public Iterator<Object> endingIterator() {
        // No recording, because the endingIterator() is used for determining size
        return childEntitySelector.endingIterator();
    }

    public ListIterator<Object> listIterator() {
        // TODO Not yet implemented
        throw new UnsupportedOperationException();
    }

    public ListIterator<Object> listIterator(int index) {
        // TODO Not yet implemented
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        return "Recording(" + childEntitySelector + ")";
    }

}
