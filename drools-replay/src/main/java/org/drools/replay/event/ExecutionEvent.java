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
package org.drools.replay.event;

import java.time.Instant;

/**
 * Base class for all recorded rule execution events.
 * Each event has a monotonically increasing sequence number and a timestamp.
 */

public abstract class ExecutionEvent {

    private final long sequenceNumber;
    private final Instant timestamp;
    private final EventType type;
    protected ExecutionEvent(long sequenceNumber, EventType type) {
        this.sequenceNumber = sequenceNumber;
        this.timestamp = Instant.now();
        this.type = type;
    }

    protected ExecutionEvent(long sequenceNumber, Instant timestamp, EventType type) {
        this.sequenceNumber = sequenceNumber;
        this.timestamp = timestamp;
        this.type = type;
    }

    public long getSequenceNumber() {
        return sequenceNumber;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public EventType getType() {
        return type;
    }

    @Override
    public String toString() {
        return String.format("[%d] %s at %s", sequenceNumber, type, timestamp);
    }
}