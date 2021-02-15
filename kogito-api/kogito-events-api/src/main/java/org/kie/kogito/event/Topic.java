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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Responsible to hold information about a topic being consumed or produced by a Kogito service
 */
public class Topic {

    private String name;
    private ChannelType type;
    private List<CloudEventMeta> eventsMeta;

    public Topic() {
        this.eventsMeta = new ArrayList<>();
    }

    public Topic(final String name, final ChannelType type) {
        this();
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ChannelType getType() {
        return type;
    }

    public void setType(ChannelType type) {
        this.type = type;
    }

    /**
     * A collection of meta information about events that can be consumed or published by this topic
     *
     * @return a list of events
     */
    public List<CloudEventMeta> getEventsMeta() {
        return Collections.unmodifiableList(this.eventsMeta);
    }

    public void setEventsMeta(List<CloudEventMeta> eventsMeta) {
        this.eventsMeta = new ArrayList<>(eventsMeta);
    }

    @Override
    public String toString() {
        return "Topic{" +
                "name='" + name + '\'' +
                ", type=" + type +
                ", events=" + eventsMeta +
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
        Topic topic = (Topic) o;
        return Objects.equals(name, topic.name) &&
                type == topic.type &&
                Objects.equals(eventsMeta, topic.eventsMeta);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type, eventsMeta);
    }
}
