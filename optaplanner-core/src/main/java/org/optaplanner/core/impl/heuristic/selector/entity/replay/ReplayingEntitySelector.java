package org.optaplanner.core.impl.heuristic.selector.entity.replay;

import java.util.Iterator;
import java.util.ListIterator;

import org.optaplanner.core.impl.domain.entity.PlanningEntityDescriptor;
import org.optaplanner.core.impl.heuristic.selector.common.iterator.SelectionIterator;
import org.optaplanner.core.impl.heuristic.selector.entity.AbstractEntitySelector;

public class ReplayingEntitySelector extends AbstractEntitySelector {

    protected final RecordingEntitySelector recordingEntitySelector;

    public ReplayingEntitySelector(RecordingEntitySelector recordingEntitySelector) {
        this.recordingEntitySelector = recordingEntitySelector;
        // No solverPhaseLifecycleSupport because the RecordingEntitySelector is hooked up elsewhere too
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public PlanningEntityDescriptor getEntityDescriptor() {
        return recordingEntitySelector.getEntityDescriptor();
    }

    public boolean isContinuous() {
        return recordingEntitySelector.isContinuous();
    }

    public boolean isNeverEnding() {
        return recordingEntitySelector.isNeverEnding();
    }

    public long getSize() {
        return recordingEntitySelector.getSize();
    }

    public Iterator<Object> iterator() {
        return new ReplayingEntityIterator();
    }

    private class ReplayingEntityIterator extends SelectionIterator<Object> {

        public boolean hasNext() {
            return recordingEntitySelector.hasRecordedSelection();
        }

        public Object next() {
            return recordingEntitySelector.getRecordedSelection();
        }

    }

    public Iterator<Object> endingIterator() {
        // No replaying, because the endingIterator() is used for determining size
        return recordingEntitySelector.endingIterator();
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
        return "Replaying(" + recordingEntitySelector + ")";
    }

}
