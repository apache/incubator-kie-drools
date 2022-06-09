package org.optaplanner.core.impl.partitionedsearch.queue;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.impl.heuristic.selector.common.iterator.UpcomingSelectionIterator;
import org.optaplanner.core.impl.partitionedsearch.scope.PartitionChangeMove;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is thread-safe.
 *
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
public class PartitionQueue<Solution_> implements Iterable<PartitionChangeMove<Solution_>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(PartitionQueue.class);

    private BlockingQueue<PartitionChangedEvent<Solution_>> queue;
    private Map<Integer, PartitionChangedEvent<Solution_>> moveEventMap; // Key is partIndex

    // Only used by producers
    private final Map<Integer, AtomicLong> nextEventIndexMap;

    // Only used by consumer
    private int openPartCount;
    private long partsCalculationCount;
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
        partsCalculationCount = 0L;
        // HashMap because only the consumer thread uses it
        processedEventIndexMap = new HashMap<>(partCount);
        for (int i = 0; i < partCount; i++) {
            processedEventIndexMap.put(i, -1L);
        }
    }

    /**
     * This method is thread-safe.
     * The previous move(s) for this partIndex (if it hasn't been consumed yet), will be skipped during iteration.
     *
     * @param partIndex {@code 0 <= partIndex < partCount}
     * @param move never null
     * @see BlockingQueue#add(Object)
     */
    public void addMove(int partIndex, PartitionChangeMove<Solution_> move) {
        long eventIndex = nextEventIndexMap.get(partIndex).getAndIncrement();
        PartitionChangedEvent<Solution_> event = new PartitionChangedEvent<>(
                partIndex, eventIndex, move);
        moveEventMap.put(event.getPartIndex(), event);
        queue.add(event);
    }

    /**
     * This method is thread-safe.
     * The previous move for this partIndex (that haven't been consumed yet), will still be returned during iteration.
     *
     * @param partIndex {@code 0 <= partIndex < partCount}
     * @param partCalculationCount at least 0
     * @see BlockingQueue#add(Object)
     */
    public void addFinish(int partIndex, long partCalculationCount) {
        long eventIndex = nextEventIndexMap.get(partIndex).getAndIncrement();
        PartitionChangedEvent<Solution_> event = new PartitionChangedEvent<>(
                partIndex, eventIndex, partCalculationCount);
        queue.add(event);
    }

    /**
     * This method is thread-safe.
     * The previous move for this partIndex (if it hasn't been consumed yet), will still be returned during iteration
     * before the iteration throws an exception.
     *
     * @param partIndex {@code 0 <= partIndex < partCount}
     * @param throwable never null
     * @see BlockingQueue#add(Object)
     */
    public void addExceptionThrown(int partIndex, Throwable throwable) {
        long eventIndex = nextEventIndexMap.get(partIndex).getAndIncrement();
        PartitionChangedEvent<Solution_> event = new PartitionChangedEvent<>(
                partIndex, eventIndex, throwable);
        queue.add(event);
    }

    @Override
    public Iterator<PartitionChangeMove<Solution_>> iterator() {
        // TODO Currently doesn't be support to be called twice on the same instance
        return new PartitionQueueIterator();
    }

    private class PartitionQueueIterator extends UpcomingSelectionIterator<PartitionChangeMove<Solution_>> {

        @Override
        protected PartitionChangeMove<Solution_> createUpcomingSelection() {
            while (true) {
                PartitionChangedEvent<Solution_> triggerEvent;
                try {
                    triggerEvent = queue.take();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new IllegalStateException("Solver thread was interrupted in Partitioned Search.", e);
                }
                switch (triggerEvent.getType()) {
                    case MOVE:
                        int partIndex = triggerEvent.getPartIndex();
                        long processedEventIndex = processedEventIndexMap.get(partIndex);
                        if (triggerEvent.getEventIndex() <= processedEventIndex) {
                            // Skip this one because it or a better version was already processed
                            LOGGER.trace("    Skipped event of partIndex ({}).", partIndex);
                            continue;
                        }
                        PartitionChangedEvent<Solution_> latestMoveEvent = moveEventMap.get(partIndex);
                        processedEventIndexMap.put(partIndex, latestMoveEvent.getEventIndex());
                        return latestMoveEvent.getMove();
                    case FINISHED:
                        openPartCount--;
                        partsCalculationCount += triggerEvent.getPartCalculationCount();
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

    public long getPartsCalculationCount() {
        return partsCalculationCount;
    }

}
