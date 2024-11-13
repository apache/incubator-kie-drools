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
package org.kie.kogito.serverless.workflow.fluent;

import java.time.Duration;
import java.util.Collection;
import java.util.HashSet;

import com.fasterxml.jackson.databind.JsonNode;

import io.serverlessworkflow.api.filters.StateDataFilter;
import io.serverlessworkflow.api.states.DefaultState;
import io.serverlessworkflow.api.timeouts.StateExecTimeout;
import io.serverlessworkflow.api.timeouts.TimeoutsDefinition;

public abstract class StateBuilder<T extends StateBuilder<T, S>, S extends DefaultState> {

    public static InjectStateBuilder inject(JsonNode data) {
        return new InjectStateBuilder(data);
    }

    public static OperationStateBuilder operation() {
        return new OperationStateBuilder();
    }

    public static ParallelStateBuilder parallel() {
        return new ParallelStateBuilder();
    }

    public static CallbackStateBuilder callback(ActionBuilder action, EventDefBuilder event) {
        return new CallbackStateBuilder(event, action);
    }

    public static EventStateBuilder event() {
        return new EventStateBuilder();
    }

    public static ForEachStateBuilder forEach(String inputExpr) {
        return new ForEachStateBuilder(inputExpr);
    }

    protected final S state;
    protected final Collection<FunctionBuilder> functionDefinitions = new HashSet<>();
    protected final Collection<EventDefBuilder> eventDefinitions = new HashSet<>();
    private short buildCount;

    Collection<FunctionBuilder> getFunctions() {
        return functionDefinitions;
    }

    Collection<EventDefBuilder> getEvents() {
        return eventDefinitions;
    }

    protected StateBuilder(S state) {
        this.state = state;
    }

    public T name(String name) {
        state.withName(name);
        return (T) this;
    }

    public T stateTimeout(Duration duration) {
        timeouts().withStateExecTimeout(new StateExecTimeout().withSingle(duration.toString()));
        return (T) this;
    }

    public T eventTimeout(Duration duration) {
        timeouts().withEventTimeout(duration.toString());
        return (T) this;
    }

    private TimeoutsDefinition timeouts() {
        TimeoutsDefinition timeouts = state.getTimeouts();
        if (timeouts == null) {
            timeouts = new TimeoutsDefinition();
            state.withTimeouts(timeouts);
        }
        return timeouts;
    }

    private StateDataFilter getFilter() {
        StateDataFilter filter = state.getStateDataFilter();
        if (filter == null) {
            filter = new StateDataFilter();
            state.withStateDataFilter(filter);
        }
        return filter;
    }

    public T inputFilter(String filter) {
        getFilter().withInput(filter);
        return (T) this;
    }

    public T outputFilter(String filter) {
        getFilter().withOutput(filter);
        return (T) this;
    }

    public S build() {
        buildCount++;
        return ensureName(state);
    }

    short buildCount() {
        return buildCount;
    }

    private static int counter;

    protected static <T extends DefaultState> T ensureName(T state) {
        if (state.getName() == null) {
            state.setName(state.getType() + "_" + counter++);
        }
        return state;
    }
}
