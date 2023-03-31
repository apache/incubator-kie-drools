package org.optaplanner.core.impl.heuristic.selector.list.mimic;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import org.optaplanner.core.impl.domain.variable.descriptor.ListVariableDescriptor;
import org.optaplanner.core.impl.heuristic.selector.AbstractSelector;
import org.optaplanner.core.impl.heuristic.selector.common.iterator.SelectionIterator;
import org.optaplanner.core.impl.heuristic.selector.list.SubList;
import org.optaplanner.core.impl.heuristic.selector.list.SubListSelector;

public class MimicRecordingSubListSelector<Solution_> extends AbstractSelector<Solution_>
        implements SubListMimicRecorder<Solution_>, SubListSelector<Solution_> {

    protected final SubListSelector<Solution_> childSubListSelector;

    protected final List<MimicReplayingSubListSelector<Solution_>> replayingSubListSelectorList;

    public MimicRecordingSubListSelector(SubListSelector<Solution_> childSubListSelector) {
        this.childSubListSelector = childSubListSelector;
        phaseLifecycleSupport.addEventListener(childSubListSelector);
        replayingSubListSelectorList = new ArrayList<>();
    }

    @Override
    public void addMimicReplayingSubListSelector(MimicReplayingSubListSelector<Solution_> replayingSubListSelector) {
        replayingSubListSelectorList.add(replayingSubListSelector);
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public ListVariableDescriptor<Solution_> getVariableDescriptor() {
        return childSubListSelector.getVariableDescriptor();
    }

    @Override
    public boolean isCountable() {
        return childSubListSelector.isCountable();
    }

    @Override
    public boolean isNeverEnding() {
        return childSubListSelector.isNeverEnding();
    }

    @Override
    public long getSize() {
        return childSubListSelector.getSize();
    }

    @Override
    public Iterator<SubList> iterator() {
        return new RecordingSubListIterator(childSubListSelector.iterator());
    }

    private class RecordingSubListIterator extends SelectionIterator<SubList> {

        private final Iterator<SubList> childSubListIterator;

        public RecordingSubListIterator(Iterator<SubList> childSubListIterator) {
            this.childSubListIterator = childSubListIterator;
        }

        @Override
        public boolean hasNext() {
            boolean hasNext = childSubListIterator.hasNext();
            for (MimicReplayingSubListSelector<Solution_> replayingValueSelector : replayingSubListSelectorList) {
                replayingValueSelector.recordedHasNext(hasNext);
            }
            return hasNext;
        }

        @Override
        public SubList next() {
            SubList next = childSubListIterator.next();
            for (MimicReplayingSubListSelector<Solution_> replayingValueSelector : replayingSubListSelectorList) {
                replayingValueSelector.recordedNext(next);
            }
            return next;
        }

    }

    @Override
    public Iterator<Object> endingValueIterator() {
        // No recording, because the endingIterator() is used for determining size
        return childSubListSelector.endingValueIterator();
    }

    @Override
    public long getValueCount() {
        return childSubListSelector.getValueCount();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;
        if (other == null || getClass() != other.getClass())
            return false;
        MimicRecordingSubListSelector<?> that = (MimicRecordingSubListSelector<?>) other;
        /*
         * Using list size in order to prevent recursion in equals/hashcode.
         * Since the replaying selector will always point back to this instance,
         * we only need to know if the lists are the same
         * in order to be able to tell if two instances are equal.
         */
        return Objects.equals(childSubListSelector, that.childSubListSelector)
                && Objects.equals(replayingSubListSelectorList.size(), that.replayingSubListSelectorList.size());
    }

    @Override
    public int hashCode() {
        return Objects.hash(childSubListSelector, replayingSubListSelectorList.size());
    }

    @Override
    public String toString() {
        return "Recording(" + childSubListSelector + ")";
    }

}
