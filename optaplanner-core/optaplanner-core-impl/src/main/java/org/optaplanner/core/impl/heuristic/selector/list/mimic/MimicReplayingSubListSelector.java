package org.optaplanner.core.impl.heuristic.selector.list.mimic;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

import org.optaplanner.core.impl.domain.variable.descriptor.ListVariableDescriptor;
import org.optaplanner.core.impl.heuristic.selector.AbstractSelector;
import org.optaplanner.core.impl.heuristic.selector.common.iterator.SelectionIterator;
import org.optaplanner.core.impl.heuristic.selector.list.SubList;
import org.optaplanner.core.impl.heuristic.selector.list.SubListSelector;
import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;

public class MimicReplayingSubListSelector<Solution_> extends AbstractSelector<Solution_>
        implements SubListSelector<Solution_> {

    protected final SubListMimicRecorder<Solution_> subListMimicRecorder;

    protected boolean hasRecordingCreated;
    protected boolean hasRecording;
    protected boolean recordingCreated;
    protected SubList recording;
    protected boolean recordingAlreadyReturned;

    public MimicReplayingSubListSelector(SubListMimicRecorder<Solution_> subListMimicRecorder) {
        this.subListMimicRecorder = subListMimicRecorder;
        // No PhaseLifecycleSupport because the MimicRecordingSubListSelector is hooked up elsewhere too
        subListMimicRecorder.addMimicReplayingSubListSelector(this);
        // Precondition for iterator(Object)'s current implementation
        if (!subListMimicRecorder.getVariableDescriptor().isValueRangeEntityIndependent()) {
            throw new IllegalArgumentException(
                    "The current implementation support only an entityIndependent variable ("
                            + subListMimicRecorder.getVariableDescriptor() + ").");
        }
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public void phaseStarted(AbstractPhaseScope<Solution_> phaseScope) {
        super.phaseStarted(phaseScope);
        // Doing this in phaseStarted instead of stepStarted due to QueuedValuePlacer compatibility
        hasRecordingCreated = false;
        recordingCreated = false;
    }

    @Override
    public void phaseEnded(AbstractPhaseScope<Solution_> phaseScope) {
        super.phaseEnded(phaseScope);
        // Doing this in phaseEnded instead of stepEnded due to QueuedValuePlacer compatibility
        hasRecordingCreated = false;
        hasRecording = false;
        recordingCreated = false;
        recording = null;
    }

    @Override
    public ListVariableDescriptor<Solution_> getVariableDescriptor() {
        return subListMimicRecorder.getVariableDescriptor();
    }

    @Override
    public boolean isCountable() {
        return subListMimicRecorder.isCountable();
    }

    @Override
    public boolean isNeverEnding() {
        return subListMimicRecorder.isNeverEnding();
    }

    @Override
    public long getSize() {
        return subListMimicRecorder.getSize();
    }

    @Override
    public Iterator<Object> endingValueIterator() {
        // No replaying, because the endingIterator() is used for determining size
        return subListMimicRecorder.endingValueIterator();
    }

    @Override
    public long getValueCount() {
        return subListMimicRecorder.getValueCount();
    }

    @Override
    public Iterator<SubList> iterator() {
        return new ReplayingSubListIterator();
    }

    public void recordedHasNext(boolean hasNext) {
        hasRecordingCreated = true;
        hasRecording = hasNext;
        recordingCreated = false;
        recording = null;
        recordingAlreadyReturned = false;
    }

    public void recordedNext(SubList next) {
        hasRecordingCreated = true;
        hasRecording = true;
        recordingCreated = true;
        recording = next;
        recordingAlreadyReturned = false;
    }

    private class ReplayingSubListIterator extends SelectionIterator<SubList> {

        private ReplayingSubListIterator() {
            // Reset so the last recording plays again even if it has already played
            recordingAlreadyReturned = false;
        }

        @Override
        public boolean hasNext() {
            if (!hasRecordingCreated) {
                throw new IllegalStateException("Replay must occur after record."
                        + " The recordingSubListSelector (" + subListMimicRecorder
                        + ")'s hasNext() has not been called yet. ");
            }
            return hasRecording && !recordingAlreadyReturned;
        }

        @Override
        public SubList next() {
            if (!recordingCreated) {
                throw new IllegalStateException("Replay must occur after record."
                        + " The recordingSubListSelector (" + subListMimicRecorder
                        + ")'s next() has not been called yet. ");
            }
            if (recordingAlreadyReturned) {
                throw new NoSuchElementException("The recordingAlreadyReturned (" + recordingAlreadyReturned
                        + ") is impossible. Check if hasNext() returns true before this call.");
            }
            // Until the recorder records something, this iterator has no next.
            recordingAlreadyReturned = true;
            return recording;
        }

        @Override
        public String toString() {
            if (hasRecordingCreated && !hasRecording) {
                return "No next replay";
            }
            return "Next replay (" + (recordingCreated ? recording : "?") + ")";
        }

    }

    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;
        if (other == null || getClass() != other.getClass())
            return false;
        MimicReplayingSubListSelector<?> that = (MimicReplayingSubListSelector<?>) other;
        return Objects.equals(subListMimicRecorder, that.subListMimicRecorder);
    }

    @Override
    public int hashCode() {
        return Objects.hash(subListMimicRecorder);
    }

    @Override
    public String toString() {
        return "Replaying(" + subListMimicRecorder + ")";
    }

}
