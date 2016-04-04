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

import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.heuristic.selector.common.iterator.SelectionIterator;
import org.optaplanner.core.impl.heuristic.selector.entity.AbstractEntitySelector;
import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;

public class MimicReplayingEntitySelector extends AbstractEntitySelector {

    protected final EntityMimicRecorder entityMimicRecorder;

    protected boolean hasRecordingCreated;
    protected boolean hasRecording;
    protected boolean recordingCreated;
    protected Object recording;
    protected boolean recordingAlreadyReturned;

    public MimicReplayingEntitySelector(EntityMimicRecorder entityMimicRecorder) {
        this.entityMimicRecorder = entityMimicRecorder;
        // No PhaseLifecycleSupport because the MimicRecordingEntitySelector is hooked up elsewhere too
        entityMimicRecorder.addMimicReplayingEntitySelector(this);
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public void phaseStarted(AbstractPhaseScope phaseScope) {
        super.phaseStarted(phaseScope);
        // Doing this in phaseStarted instead of stepStarted due to QueuedEntityPlacer compatibility
        hasRecordingCreated = false;
        recordingCreated = false;
    }

    @Override
    public void phaseEnded(AbstractPhaseScope phaseScope) {
        super.phaseEnded(phaseScope);
        // Doing this in phaseEnded instead of stepEnded due to QueuedEntityPlacer compatibility
        hasRecordingCreated = false;
        hasRecording = false;
        recordingCreated = false;
        recording = null;
    }

    @Override
    public EntityDescriptor getEntityDescriptor() {
        return entityMimicRecorder.getEntityDescriptor();
    }

    @Override
    public boolean isCountable() {
        return entityMimicRecorder.isCountable();
    }

    @Override
    public boolean isNeverEnding() {
        return entityMimicRecorder.isNeverEnding();
    }

    @Override
    public long getSize() {
        return entityMimicRecorder.getSize();
    }

    @Override
    public Iterator<Object> iterator() {
        return new ReplayingEntityIterator();
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

    private class ReplayingEntityIterator extends SelectionIterator<Object> {

        private ReplayingEntityIterator() {
            // Reset so the last recording plays again even if it has already played
            recordingAlreadyReturned = false;
        }

        @Override
        public boolean hasNext() {
            if (!hasRecordingCreated) {
                throw new IllegalStateException("Replay must occur after record."
                        + " The recordingEntitySelector (" + entityMimicRecorder
                        + ")'s hasNext() has not been called yet. ");
            }
            return hasRecording && !recordingAlreadyReturned;
        }

        @Override
        public Object next() {
            if (!recordingCreated) {
                throw new IllegalStateException("Replay must occur after record."
                        + " The recordingEntitySelector (" + entityMimicRecorder
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
    public Iterator<Object> endingIterator() {
        // No replaying, because the endingIterator() is used for determining size
        return entityMimicRecorder.endingIterator();
    }

    @Override
    public ListIterator<Object> listIterator() {
        // TODO Not yet implemented
        throw new UnsupportedOperationException();
    }

    @Override
    public ListIterator<Object> listIterator(int index) {
        // TODO Not yet implemented
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        return "Replaying(" + entityMimicRecorder + ")";
    }

}
