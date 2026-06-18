/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.optaplanner.core.impl.heuristic.selector.entity.mimic;

import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Objects;

import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.heuristic.selector.AbstractDemandEnabledSelector;
import org.optaplanner.core.impl.heuristic.selector.common.iterator.SelectionIterator;
import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;

public class MimicReplayingEntitySelector<Solution_>
        extends AbstractDemandEnabledSelector<Solution_>
        implements org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector<Solution_> {

    private final EntityMimicRecorder<Solution_> entityMimicRecorder;

    private boolean hasRecordingCreated;
    private boolean hasRecording;
    private boolean recordingCreated;
    private Object recording;
    private boolean recordingAlreadyReturned;

    public MimicReplayingEntitySelector(EntityMimicRecorder<Solution_> entityMimicRecorder) {
        this.entityMimicRecorder = entityMimicRecorder;
        // No PhaseLifecycleSupport because the MimicRecordingEntitySelector is hooked up elsewhere too
        entityMimicRecorder.addMimicReplayingEntitySelector(this);
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public void phaseStarted(AbstractPhaseScope<Solution_> phaseScope) {
        super.phaseStarted(phaseScope);
        // Doing this in phaseStarted instead of stepStarted due to QueuedEntityPlacer compatibility
        hasRecordingCreated = false;
        recordingCreated = false;
    }

    @Override
    public void phaseEnded(AbstractPhaseScope<Solution_> phaseScope) {
        super.phaseEnded(phaseScope);
        // Doing this in phaseEnded instead of stepEnded due to QueuedEntityPlacer compatibility
        hasRecordingCreated = false;
        hasRecording = false;
        recordingCreated = false;
        recording = null;
    }

    @Override
    public EntityDescriptor<Solution_> getEntityDescriptor() {
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
    public boolean equals(Object other) {
        if (this == other)
            return true;
        if (other == null || getClass() != other.getClass())
            return false;
        MimicReplayingEntitySelector<?> that = (MimicReplayingEntitySelector<?>) other;
        return Objects.equals(entityMimicRecorder, that.entityMimicRecorder);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entityMimicRecorder);
    }

    @Override
    public String toString() {
        return "Replaying(" + entityMimicRecorder + ")";
    }

}
