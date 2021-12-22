/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.kogito.event;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Represents the metadata definition for an event emitted or listened in the engine.
 * It's based on the CloudEvents specification to help consumers and producers to be aware of the required events being
 * consumed by the runtime engine.
 */
public class CloudEventMeta implements EventMeta {

    private String type;
    private String source;
    private EventKind kind;

    public CloudEventMeta() {

    }

    public CloudEventMeta(final String type, final String source, final EventKind kind) {
        this.source = source;
        this.type = type;
        this.kind = kind;
    }

    @Override
    @JsonIgnore
    public String getSpecVersion() {
        return AbstractDataEvent.SPEC_VERSION;
    }

    /**
     * Gets the source for an instance of a CloudEvent
     *
     * @return a String containing the source for a instance of a CloudEvent
     * @see <a href="https://github.com/cloudevents/spec/blob/v1.0/spec.md#source-1">CloudEvents Source</a>
     */
    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    /**
     * Gets the type for an instance of a CloudEvent
     *
     * @return a string containing the given type for this instance
     * @see <a href="https://github.com/cloudevents/spec/blob/v1.0/spec.md#type">CloudEvents Type</a>
     */
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    /**
     * Signals with this event should be on an {@link ChannelType#INCOMING} or {@link ChannelType#OUTGOING} channel
     *
     * @return Gets the {@link EventKind} for this instance
     */
    public EventKind getKind() {
        return kind;
    }

    public void setKind(EventKind kind) {
        this.kind = kind;
    }

    @Override
    public String toString() {
        return "CloudEventMeta{" +
                "type='" + type + '\'' +
                ", source='" + source + '\'' +
                ", kind=" + kind +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CloudEventMeta that = (CloudEventMeta) o;
        return Objects.equals(type, that.type) &&
                Objects.equals(source, that.source) &&
                kind == that.kind;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, source, kind);
    }
}
