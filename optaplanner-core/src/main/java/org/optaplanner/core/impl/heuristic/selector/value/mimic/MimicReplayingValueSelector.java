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

package org.optaplanner.core.impl.heuristic.selector.value.mimic;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.heuristic.selector.common.iterator.SelectionIterator;
import org.optaplanner.core.impl.heuristic.selector.value.AbstractValueSelector;
import org.optaplanner.core.impl.heuristic.selector.value.EntityIndependentValueSelector;
import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;

public class MimicReplayingValueSelector extends AbstractValueSelector
        implements EntityIndependentValueSelector {

    protected final ValueMimicRecorder valueMimicRecorder;

    protected boolean hasRecordingCreated;
    protected boolean hasRecording;
    protected boolean recordingCreated;
    protected Object recording;
    protected boolean recordingAlreadyReturned;

    public MimicReplayingValueSelector(ValueMimicRecorder valueMimicRecorder) {
        this.valueMimicRecorder = valueMimicRecorder;
        // No PhaseLifecycleSupport because the MimicRecordingValueSelector is hooked up elsewhere too
        valueMimicRecorder.addMimicReplayingValueSelector(this);
        // Precondition for iterator(Object)'s current implementation
        if (!valueMimicRecorder.getVariableDescriptor().isValueRangeEntityIndependent()) {
            throw new IllegalArgumentException(
                    "The current implementation support only an entityIndependent variable ("
                    + valueMimicRecorder.getVariableDescriptor() + ").");
        }
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public void phaseStarted(AbstractPhaseScope phaseScope) {
        super.phaseStarted(phaseScope);
        // Doing this in phaseStarted instead of stepStarted due to QueuedValuePlacer compatibility
        hasRecordingCreated = false;
        recordingCreated = false;
    }

    @Override
    public void phaseEnded(AbstractPhaseScope phaseScope) {
        super.phaseEnded(phaseScope);
        // Doing this in phaseEnded instead of stepEnded due to QueuedValuePlacer compatibility
        hasRecordingCreated = false;
        hasRecording = false;
        recordingCreated = false;
        recording = null;
    }

    @Override
    public GenuineVariableDescriptor getVariableDescriptor() {
        return valueMimicRecorder.getVariableDescriptor();
    }

    @Override
    public boolean isCountable() {
        return valueMimicRecorder.isCountable();
    }

    @Override
    public boolean isNeverEnding() {
        return valueMimicRecorder.isNeverEnding();
    }

    @Override
    public long getSize(Object entity) {
        return valueMimicRecorder.getSize(entity);
    }

    @Override
    public long getSize() {
        return valueMimicRecorder.getSize();
    }

    @Override
    public Iterator<Object> iterator(Object entity) {
        // Ignores the entity, but the constructor of this class guarantees that the valueRange is entity independent
        return new ReplayingValueIterator();
    }

    @Override
    public Iterator<Object> iterator() {
        return new ReplayingValueIterator();
    }

    public void recordedHasNext(boolean hasNext) {
        hasRecordingCreated = true;
        hasRecording = hasNext;
        recordingCreated = false;
        recording = null;
        recordingAlreadyReturned = false;
    }

    public void recordedNext(Object next) {
        hasRecordingCreated = true;
        hasRecording = true;
        recordingCreated = true;
        recording = next;
        recordingAlreadyReturned = false;
    }

    private class ReplayingValueIterator extends SelectionIterator<Object> {

        private ReplayingValueIterator() {
            // Reset so the last recording plays again even if it has already played
            recordingAlreadyReturned = false;
        }

        @Override
        public boolean hasNext() {
            if (!hasRecordingCreated) {
                throw new IllegalStateException("Replay must occur after record."
                        + " The recordingValueSelector (" + valueMimicRecorder
                        + ")'s hasNext() has not been called yet. ");
            }
            return hasRecording && !recordingAlreadyReturned;
        }

        @Override
        public Object next() {
            if (!recordingCreated) {
                throw new IllegalStateException("Replay must occur after record."
                        + " The recordingValueSelector (" + valueMimicRecorder
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
    public Iterator<Object> endingIterator(Object entity) {
        // No replaying, because the endingIterator() is used for determining size
        return valueMimicRecorder.endingIterator(entity);
    }

    @Override
    public String toString() {
        return "Replaying(" + valueMimicRecorder + ")";
    }

}
