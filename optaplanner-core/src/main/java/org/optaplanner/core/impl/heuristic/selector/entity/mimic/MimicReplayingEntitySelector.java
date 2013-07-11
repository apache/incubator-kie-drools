package org.optaplanner.core.impl.heuristic.selector.entity.mimic;

import java.util.Iterator;
import java.util.ListIterator;

import org.optaplanner.core.impl.domain.entity.PlanningEntityDescriptor;
import org.optaplanner.core.impl.heuristic.selector.common.iterator.SelectionIterator;
import org.optaplanner.core.impl.heuristic.selector.entity.AbstractEntitySelector;

public class MimicReplayingEntitySelector extends AbstractEntitySelector {

    protected final MimicRecordingEntitySelector mimicRecordingEntitySelector;

    public MimicReplayingEntitySelector(MimicRecordingEntitySelector mimicRecordingEntitySelector) {
        this.mimicRecordingEntitySelector = mimicRecordingEntitySelector;
        // No solverPhaseLifecycleSupport because the MimicRecordingEntitySelector is hooked up elsewhere too
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public PlanningEntityDescriptor getEntityDescriptor() {
        return mimicRecordingEntitySelector.getEntityDescriptor();
    }

    public boolean isContinuous() {
        return mimicRecordingEntitySelector.isContinuous();
    }

    public boolean isNeverEnding() {
        return mimicRecordingEntitySelector.isNeverEnding();
    }

    public long getSize() {
        return mimicRecordingEntitySelector.getSize();
    }

    public Iterator<Object> iterator() {
        return new ReplayingEntityIterator();
    }

    private class ReplayingEntityIterator extends SelectionIterator<Object> {

        public boolean hasNext() {
            return mimicRecordingEntitySelector.hasRecordedSelection();
        }

        public Object next() {
            return mimicRecordingEntitySelector.getRecordedSelection();
        }

    }

    public Iterator<Object> endingIterator() {
        // No replaying, because the endingIterator() is used for determining size
        return mimicRecordingEntitySelector.endingIterator();
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
        return "Replaying(" + mimicRecordingEntitySelector + ")";
    }

}
