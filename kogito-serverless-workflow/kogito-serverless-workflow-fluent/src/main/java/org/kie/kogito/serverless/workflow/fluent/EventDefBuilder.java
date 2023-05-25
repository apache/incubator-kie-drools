/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.serverless.workflow.fluent;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import io.serverlessworkflow.api.events.EventDefinition;

public class EventDefBuilder {

    private EventDefinition eventDefinition;

    public static EventDefBuilder eventDef(String type) {
        return new EventDefBuilder(new EventDefinition().withType(type).withName(type));
    }

    public EventDefBuilder metadata(String key, String value) {
        Map<String, String> metadata = eventDefinition.getMetadata();

        if (metadata == null) {
            metadata = new HashMap<>();
            eventDefinition.withMetadata(metadata);
        }
        metadata.put(key, value);
        return this;
    }

    public EventDefBuilder dataOnly(boolean dataOnly) {
        eventDefinition.withDataOnly(dataOnly);
        return this;
    }

    public EventDefBuilder source(String source) {
        eventDefinition.withSource(source);
        return this;
    }

    private EventDefBuilder(EventDefinition eventDefinition) {
        this.eventDefinition = eventDefinition;
    }

    public EventDefinition build() {
        return eventDefinition;
    }

    public String getName() {
        return eventDefinition.getName();
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventDefinition);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        EventDefBuilder other = (EventDefBuilder) obj;
        return Objects.equals(eventDefinition.getName(), other.eventDefinition.getName());
    }
}
