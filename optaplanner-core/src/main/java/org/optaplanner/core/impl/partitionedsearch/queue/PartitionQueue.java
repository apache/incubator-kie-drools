/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.partitionedsearch.queue;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.optaplanner.core.impl.heuristic.selector.common.iterator.UpcomingSelectionIterator;
import org.optaplanner.core.impl.partitionedsearch.scope.PartitionChangeMove;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Thread-safe
 */
public class PartitionQueue<Solution_> implements Iterable<PartitionChangeMove> {

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    private BlockingQueue<PartitionChangedEvent<Solution_>> queue;
    private Map<Integer, PartitionChangedEvent<Solution_>> moveEventMap; // Key is partIndex

    // Only used by producers
    private final Map<Integer, AtomicLong> nextEventIndexMap;

    // Only used by consumer
    private int openPartCount;
    private final Map<Integer, Long> processedEventIndexMap; // Key is partIndex

    public PartitionQueue(int partCount) {
        // TODO partCount * 100 is pulled from thin air
        queue = new ArrayBlockingQueue<>(partCount * 100);
        moveEventMap = new ConcurrentHashMap<>(partCount);
        Map<Integer, AtomicLong> nextEventIndexMap = new HashMap<>(partCount);
        for (int i = 0; i < partCount; i++) {
            nextEventIndexMap.put(i, new AtomicLong(0));
        }
        this.nextEventIndexMap = Collections.unmodifiableMap(nextEventIndexMap);
        openPartCount = partCount;
        // HashMap because only the consumer thread uses it
        processedEventIndexMap = new HashMap<>(partCount);
        for (int i = 0; i < partCount; i++) {
            processedEventIndexMap.put(i, -1L);
        }
    }

    /**
     * Thread-safe method.
     * The previous move(s) for this partIndex (if it hasn't been consumed yet), will be skipped during iteration.
     * @param partIndex {@code 0 <= partIndex < partCount}
     * @param move never null
     */
    public void addMove(int partIndex, PartitionChangeMove<Solution_> move) {
        long eventIndex = nextEventIndexMap.get(partIndex).getAndIncrement();
        PartitionChangedEvent<Solution_> event = new PartitionChangedEvent<>(
                partIndex, eventIndex, move);
        moveEventMap.put(event.getPartIndex(), event);
        queue.add(event);
    }

    /**
     * Thread-safe method.
     * The previous move for this partIndex (if it hasn't been consumed yet), will still be returned during iteration.
     * @param partIndex {@code 0 <= partIndex < partCount}
     */
    public void addFinish(int partIndex) {
        long eventIndex = nextEventIndexMap.get(partIndex).getAndIncrement();
        PartitionChangedEvent<Solution_> event = new PartitionChangedEvent<>(
                partIndex, eventIndex, PartitionChangedEvent.PartitionChangedEventType.FINISHED);
        queue.add(event);
    }

    /**
     * Thread-safe method.
     * The previous move for this partIndex (if it hasn't been consumed yet), will still be returned during iteration
     * before the iteration throws an exception.
     * @param partIndex {@code 0 <= partIndex < partCount}
     * @param throwable never null
     */
    public void addExceptionThrown(int partIndex, Throwable throwable) {
        long eventIndex = nextEventIndexMap.get(partIndex).getAndIncrement();
        PartitionChangedEvent<Solution_> event = new PartitionChangedEvent<>(
                partIndex, eventIndex, throwable);
        queue.add(event);
    }

    @Override
    public Iterator<PartitionChangeMove> iterator() {
        // Currently doesn't be support to be called twice on the same instance
        return new PartitionQueueIterator();
    }

    private class PartitionQueueIterator extends UpcomingSelectionIterator<PartitionChangeMove> {

        @Override
        protected PartitionChangeMove createUpcomingSelection() {
            while (true) {
                PartitionChangedEvent<Solution_> triggerEvent;
                try {
                    triggerEvent = queue.take();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    // TODO
                    throw new IllegalStateException("TODO was interrupted.", e);
                }
                switch (triggerEvent.getType()) {
                    case MOVE:
                        int partIndex = triggerEvent.getPartIndex();
                        long processedEventIndex = processedEventIndexMap.get(partIndex);
                        if (triggerEvent.getEventIndex() <= processedEventIndex) {
                            // Skip this one because it or a better version was already processed
                            logger.trace("    Skipped event of partIndex ({}).", partIndex);
                            continue;
                        }
                        PartitionChangedEvent<Solution_> latestMoveEvent = moveEventMap.get(partIndex);
                        processedEventIndexMap.put(partIndex, latestMoveEvent.getEventIndex());
                        return latestMoveEvent.getMove();
                    case FINISHED:
                        openPartCount--;
                        if (openPartCount <= 0) {
                            return noUpcomingSelection();
                        } else {
                            continue;
                        }
                    case EXCEPTION_THROWN:
                        throw new IllegalStateException("The partition child thread with partIndex ("
                                + triggerEvent.getPartIndex() + ") has thrown an exception."
                                + " Relayed here in the parent thread.",
                                triggerEvent.getThrowable());
                    default:
                        throw new IllegalStateException("The partitionChangedEventType ("
                                + triggerEvent.getType() + ") is not implemented.");
                }
            }
        }

    }

}
