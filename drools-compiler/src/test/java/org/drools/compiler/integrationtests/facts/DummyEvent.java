/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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
package org.drools.compiler.integrationtests.facts;

import java.util.Objects;

/**
 * A simple fact class used in tests.
 */
public class DummyEvent {

    private int id;

    private long eventTimestamp;

    private String state;

    private String idA;

    private String idB;

    public DummyEvent(int id, long eventTimestamp, String idA, String idB) {
        this.id = id;
        this.eventTimestamp = eventTimestamp;
        this.idA = idA;
        this.idB = idB;
        this.state = "initial";
    }

    public String getState() {
        return state;
    }

    public DummyEvent setState(String state) {
        this.state = state;
        return this;
    }

    public int getId() {
        return id;
    }

    public DummyEvent setId(int id) {
        this.id = id;
        return this;
    }

    public long getEventTimestamp() {
        return eventTimestamp;
    }

    public DummyEvent setEventTimestamp(long eventTimestamp) {
        this.eventTimestamp = eventTimestamp;
        return this;
    }

    public String getIdA() {
        return idA;
    }

    public void setIdA(String idA) {
        this.idA = idA;
    }

    public String getIdB() {
        return idB;
    }

    public void setIdB(String idB) {
        this.idB = idB;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DummyEvent that = (DummyEvent) o;
        return Objects.equals(this.id, that.id) &&
            Objects.equals(this.eventTimestamp, that.eventTimestamp) &&
            Objects.equals(this.idA, that.idA) &&
            Objects.equals(this.idB, that.idB) &&
            Objects.equals(this.state, that.state);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, eventTimestamp, idA, idB, state);
    }

    @Override
    public String toString() {
        return "DummyEvent{" + "id=" + id + ", eventTimestamp=" + eventTimestamp + ", idA=" + idA + ", idB= " + idB + ", state=" + state + '}';
    }
}
