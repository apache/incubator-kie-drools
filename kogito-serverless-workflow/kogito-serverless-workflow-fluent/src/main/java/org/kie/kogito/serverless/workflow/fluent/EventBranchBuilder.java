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

import java.util.ArrayList;
import java.util.List;

import io.serverlessworkflow.api.actions.Action;
import io.serverlessworkflow.api.events.OnEvents;
import io.serverlessworkflow.api.filters.EventDataFilter;

public class EventBranchBuilder {

    private EventStateBuilder parent;
    private List<Action> actions = new ArrayList<>();
    private List<String> eventsRef = new ArrayList<>();
    private OnEvents onEvents;

    public EventBranchBuilder(EventStateBuilder parent, OnEvents onEvents) {
        this.parent = parent;
        this.onEvents = onEvents.withActions(actions).withEventRefs(eventsRef);
    }

    public EventBranchBuilder action(ActionBuilder action) {
        action.getFunction().ifPresent(parent.getFunctions()::add);
        action.getEvent().ifPresent(parent.getEvents()::add);
        actions.add(action.build());
        return this;
    }

    private EventDataFilter getFilter() {
        EventDataFilter eventFilter = onEvents.getEventDataFilter();
        if (eventFilter == null) {
            eventFilter = new EventDataFilter();
            onEvents.withEventDataFilter(eventFilter);
        }

        return eventFilter;
    }

    public EventBranchBuilder data(String expr) {
        getFilter().withData(expr);
        return this;
    }

    public EventBranchBuilder outputFilter(String expr) {
        getFilter().withToStateData(expr);
        return this;
    }

    public EventBranchBuilder event(EventDefBuilder event) {
        parent.getEvents().add(event);
        eventsRef.add(event.getName());
        return this;
    }

    public EventStateBuilder endBranch() {
        return parent;
    }
}
