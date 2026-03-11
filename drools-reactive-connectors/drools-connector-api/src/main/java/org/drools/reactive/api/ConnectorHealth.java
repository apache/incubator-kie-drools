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
package org.drools.reactive.api;

import java.util.Collections;
import java.util.Map;

/**
 * Health snapshot of a {@link ReactiveConnector}, including its lifecycle state,
 * cumulative message counts, and any current error.
 */
public final class ConnectorHealth {

    private final ConnectorState state;
    private final long messagesReceived;
    private final long messagesProcessed;
    private final long messagesFailed;
    private final Throwable lastError;
    private final Map<String, Object> details;

    public ConnectorHealth(ConnectorState state,
                           long messagesReceived,
                           long messagesProcessed,
                           long messagesFailed,
                           Throwable lastError,
                           Map<String, Object> details) {
        this.state = state;
        this.messagesReceived = messagesReceived;
        this.messagesProcessed = messagesProcessed;
        this.messagesFailed = messagesFailed;
        this.lastError = lastError;
        this.details = details != null ? Collections.unmodifiableMap(details) : Collections.emptyMap();
    }

    public ConnectorHealth(ConnectorState state,
                           long messagesReceived,
                           long messagesProcessed,
                           long messagesFailed) {
        this(state, messagesReceived, messagesProcessed, messagesFailed, null, null);
    }

    public ConnectorState getState() {
        return state;
    }

    public long getMessagesReceived() {
        return messagesReceived;
    }

    public long getMessagesProcessed() {
        return messagesProcessed;
    }

    public long getMessagesFailed() {
        return messagesFailed;
    }

    public Throwable getLastError() {
        return lastError;
    }

    public Map<String, Object> getDetails() {
        return details;
    }

    @Override
    public String toString() {
        return "ConnectorHealth{" +
                "state=" + state +
                ", received=" + messagesReceived +
                ", processed=" + messagesProcessed +
                ", failed=" + messagesFailed +
                (lastError != null ? ", lastError=" + lastError.getMessage() : "") +
                '}';
    }
}
