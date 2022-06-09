package org.optaplanner.core.impl.heuristic.selector.value.mimic;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.heuristic.selector.common.iterator.SelectionIterator;
import org.optaplanner.core.impl.heuristic.selector.value.AbstractValueSelector;
import org.optaplanner.core.impl.heuristic.selector.value.EntityIndependentValueSelector;

public class MimicRecordingValueSelector<Solution_> extends AbstractValueSelector<Solution_>
        implements ValueMimicRecorder<Solution_>, EntityIndependentValueSelector<Solution_> {

    protected final EntityIndependentValueSelector<Solution_> childValueSelector;

    protected final List<MimicReplayingValueSelector<Solution_>> replayingValueSelectorList;

    public MimicRecordingValueSelector(EntityIndependentValueSelector<Solution_> childValueSelector) {
        this.childValueSelector = childValueSelector;
        phaseLifecycleSupport.addEventListener(childValueSelector);
        replayingValueSelectorList = new ArrayList<>();
    }

    @Override
    public void addMimicReplayingValueSelector(MimicReplayingValueSelector<Solution_> replayingEntitySelector) {
        replayingValueSelectorList.add(replayingEntitySelector);
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
    public String toString() {
        return "Recording(" + childValueSelector + ")";
    }

}
