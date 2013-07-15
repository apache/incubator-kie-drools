package org.optaplanner.core.impl.heuristic.selector.entity.mimic;

import java.util.Iterator;
import java.util.ListIterator;

import org.optaplanner.core.impl.domain.entity.PlanningEntityDescriptor;
import org.optaplanner.core.impl.heuristic.selector.common.iterator.SelectionIterator;
import org.optaplanner.core.impl.heuristic.selector.entity.AbstractEntitySelector;
import org.optaplanner.core.impl.phase.AbstractSolverPhaseScope;
import org.optaplanner.core.impl.phase.step.AbstractStepScope;

public class MimicReplayingEntitySelector extends AbstractEntitySelector {

    protected final MimicRecordingEntitySelector recordingEntitySelector;

    protected boolean hasRecordedSelectionCreated;
    protected boolean hasRecordedSelection;
    protected boolean recordedSelectionCreated;
    protected Object recordedSelection;

    public MimicReplayingEntitySelector(MimicRecordingEntitySelector recordingEntitySelector) {
        this.recordingEntitySelector = recordingEntitySelector;
        // No solverPhaseLifecycleSupport because the MimicRecordingEntitySelector is hooked up elsewhere too
        recordingEntitySelector.addMimicReplayingEntitySelector(this);
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public void phaseStarted(AbstractSolverPhaseScope phaseScope) {
        super.phaseStarted(phaseScope);
        // Doing this in phaseStarted instead of stepStarted due to EntityPlacer compatibility
        hasRecordedSelectionCreated = false;
        recordedSelectionCreated = false;
    }

    @Override
    public void stepEnded(AbstractStepScope stepScope) {
        super.stepEnded(stepScope);
        hasRecordedSelectionCreated = false;
        hasRecordedSelection = false;
        recordedSelectionCreated = false;
        recordedSelection = null;
    }

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

    public void recordedHasNext(boolean hasNext) {
        hasRecordedSelection = hasNext;
        hasRecordedSelectionCreated = true;
    }

    public void recordedNext(Object next) {
        recordedSelection = next;
        recordedSelectionCreated = true;
    }

    private class ReplayingEntityIterator extends SelectionIterator<Object> {

        public boolean hasNext() {
            if (!hasRecordedSelectionCreated) {
                throw new IllegalStateException("Replay must occur after record."
                        + " The recordingEntitySelector (" + recordingEntitySelector
                        + ")'s hasNext() has not been called yet. ");
            }
            return hasRecordedSelection;
        }

        public Object next() {
            if (!recordedSelectionCreated) {
                throw new IllegalStateException("Replay must occur after record."
                        + " The recordingEntitySelector (" + recordingEntitySelector
                        + ")'s next() has not been called yet. ");
            }
            Object next = recordedSelection;
            // Until the recorder records something, this iterator has no next.
            hasRecordedSelection = false;
            recordedSelection = null;
            return next;
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
