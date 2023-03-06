/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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

package org.kie.api.runtime.conf;


import java.util.Objects;

/**
 * A class for the session persistence configuration.
 */
public class PersistedSessionOption implements SingleValueKieSessionOption {

    public enum Strategy {
        FULL, STORES_ONLY
    }

    /**
     * The property name for the clock type configuration
     */
    public static final String PROPERTY_NAME = "drools.persistedsession";

    private final long sessionId;

    private final Strategy strategy;

    private PersistedSessionOption() {
        this(-1L);
    }

    private PersistedSessionOption(long sessionId) {
        this(sessionId, Strategy.FULL);
    }

    private PersistedSessionOption(Strategy strategy) {
        this(-1, strategy);
    }

    private PersistedSessionOption(long sessionId, Strategy strategy) {
        this.sessionId = sessionId;
        this.strategy = strategy;
    }

    public static PersistedSessionOption newSession() {
        return new PersistedSessionOption();
    }

    public static PersistedSessionOption newSession(Strategy strategy) {
        return new PersistedSessionOption(strategy);
    }

    public static PersistedSessionOption fromSession(long sessionId) {
        return new PersistedSessionOption(sessionId);
    }

    public static PersistedSessionOption fromSession(long sessionId, Strategy strategy) {
        return new PersistedSessionOption(sessionId, strategy);
    }

    /**
     * {@inheritDoc}
     */
    public String getPropertyName() {
        return PROPERTY_NAME;
    }

    public long getSessionId() {
        return sessionId;
    }

    public Strategy getStrategy() {
        return strategy;
    }

    public boolean isNewSession() {
        return sessionId < 0;
    }

    @Override
    public String toString() {
        return "PersistedSessionOption( "+ sessionId +" )";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PersistedSessionOption that = (PersistedSessionOption) o;
        return sessionId == that.sessionId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(sessionId);
    }
}
