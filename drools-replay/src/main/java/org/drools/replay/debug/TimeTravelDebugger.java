/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.replay.debug;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.drools.replay.event.EventType;
import org.drools.replay.event.ExecutionEvent;
import org.drools.replay.event.FactDeletedEvent;
import org.drools.replay.event.FactInsertedEvent;
import org.drools.replay.event.FactUpdatedEvent;
import org.drools.replay.event.RuleMatchEvent;
import org.drools.replay.recorder.ExecutionLog;
/**
 * Provides time-travel debugging over a recorded {@link ExecutionLog}.
 * Allows stepping forward/backward through events and inspecting the
 * reconstructed state of working memory at any point.
 *
 * <p>Usage:
 * <pre>{@code
 * ExecutionLog log = recorder.getLog();
 * TimeTravelDebugger debugger = new TimeTravelDebugger(log);
 *
 * // Step through events
 * ExecutionEvent e1 = debugger.stepForward();
 * ExecutionEvent e2 = debugger.stepForward();
 *
 * // Inspect state at current position
 * StateSnapshot snapshot = debugger.getStateSnapshot();
 *
 * // Jump to a specific position
 * debugger.jumpTo(5);
 *
 * // Step backward
 * ExecutionEvent prev = debugger.stepBackward();
 *
 * // Query history for a specific rule
 * List<RuleMatchEvent> history = debugger.getRuleFireHistory("myRule");
 * }</pre>
 */

public class TimeTravelDebugger {
    private final ExecutionLog log;
    private int currentPosition = -1;
    public TimeTravelDebugger(ExecutionLog log) {
        this.log = log;
    }
    /**
     * Advances the cursor by one event and returns it.
     *
     * @return the next event, or null if at the end
     */
    public ExecutionEvent stepForward() {
        List<ExecutionEvent> events = log.getEvents();
        if (currentPosition + 1 < events.size()) {
            currentPosition++;
            return events.get(currentPosition);
        }
        return null;
    }
    /**
     * Moves the cursor back by one event and returns the event at the new position.
     *
     * @return the event at the new position, or null if already at the beginning
     */
    public ExecutionEvent stepBackward() {
        if (currentPosition > 0) {
            currentPosition-- ;
            return log.getEvents().get(currentPosition);
        } else if (currentPosition == 0) {
            currentPosition = -1;
            return null;
        }
        return null;
    }
    /**
     * Jumps to the specified position in the event log.
     *
     * @param position the 0-based position to jump to, or -1 to reset to before the first event
     * @return the event at the target position, or null if position is -1
     * @throws IndexOutOfBoundsException if position is out of range
     */
    public ExecutionEvent jumpTo(int position) {
        List<ExecutionEvent> events = log.getEvents();
        if (position < -1 || position >= events.size()) {
            throw new IndexOutOfBoundsException(
                    "Position " + position + " out of range [-1, " + (events.size() - 1) + "]");
        }
        currentPosition = position;
        return position >= 0 ? events.get(position) : null;
    }
    public int getCurrentPosition() {
        return currentPosition;
    }
    public int getTotalEvents() {
        return log.size();
    }
    public boolean hasNext() {
        return currentPosition + 1 < log.size();
    }
    public boolean hasPrevious() {
        return currentPosition > -1;
    }
    /**
     * Returns the event at the given position without moving the cursor.
     */
    public ExecutionEvent peekAt(int position) {
        return log.getEvent(position);
    }
    /**
     * Returns all events in the specified range (inclusive).
     */
    public List<ExecutionEvent> getEventsInRange(int fromInclusive, int toInclusive) {
        List<ExecutionEvent> events = log.getEvents();
        return events.subList(
                Math.max(0, fromInclusive),
                Math.min(events.size(), toInclusive + 1));
    }
    /**
     * Reconstructs the state of working memory at the current cursor position
     * by replaying fact insert/update/delete events from the beginning.
     */
    public StateSnapshot getStateSnapshot() {
        List<ExecutionEvent> events = log.getEvents();
        Map<String, String> activeFacts = new LinkedHashMap<>();
        List<String> rulesFired = new ArrayList<>();
        int upTo = Math.min(currentPosition + 1, events.size());
        for (int i = 0; i < upTo; i++) {
            ExecutionEvent event = events.get(i);
            applyEvent(event, activeFacts, rulesFired);
        }
        return new StateSnapshot(currentPosition, activeFacts, rulesFired, upTo);
    }
    /**
     * Returns all AFTER_RULE_FIRED events for the given rule name.
     */
    public List<RuleMatchEvent> getRuleFireHistory(String ruleName) {
        return log.getEvents().stream()
                .filter(e -> e.getType() == EventType.AFTER_RULE_FIRED)
                .filter(RuleMatchEvent.class::isInstance)
                .map(RuleMatchEvent.class::cast)
                .filter(e -> e.getRuleName().equals(ruleName))
                .collect(Collectors.toList());
    }
    /**
     * Returns all fact-related events for the given fully-qualified class name.
     */
    public List<ExecutionEvent> getFactHistory(String factClassName) {
        return log.getEvents().stream()
                .filter(e -> {
                    if (e instanceof FactInsertedEvent) {
                        return ((FactInsertedEvent) e).getFactClassName().equals(factClassName);
                    } else if (e instanceof FactUpdatedEvent) {
                        return ((FactUpdatedEvent) e).getFactClassName().equals(factClassName);
                    } else if (e instanceof FactDeletedEvent) {
                        return ((FactDeletedEvent) e).getFactClassName().equals(factClassName);
                    }
                    return false;
                })
                .collect(Collectors.toList());
    }
    private void applyEvent(ExecutionEvent event, Map<String, String> activeFacts,
                            List<String> rulesFired) {
        switch (event.getType()) {
            case FACT_INSERTED:
                FactInsertedEvent inserted = (FactInsertedEvent) event;
                activeFacts.put(inserted.getFactIdentity(), inserted.getFactToString());
                break;
            case FACT_UPDATED:
                FactUpdatedEvent updated = (FactUpdatedEvent) event;
                activeFacts.put(updated.getFactIdentity(), updated.getNewFactToString());
                break;
            case FACT_DELETED:
                FactDeletedEvent deleted = (FactDeletedEvent) event;
                activeFacts.remove(deleted.getFactIdentity());
                break;
            case AFTER_RULE_FIRED:
                if (event instanceof RuleMatchEvent) {
                    rulesFired.add(((RuleMatchEvent) event).getRuleName());
                }
                break;
            default:
                break;
        }
    }
}