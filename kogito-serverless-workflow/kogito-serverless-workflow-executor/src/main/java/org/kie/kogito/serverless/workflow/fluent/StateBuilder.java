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
import java.util.Collection;

import com.fasterxml.jackson.databind.JsonNode;

import io.serverlessworkflow.api.end.End;
import io.serverlessworkflow.api.filters.StateDataFilter;
import io.serverlessworkflow.api.states.DefaultState;

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

    public static ForEachStateBuilder forEach(String inputExpr) {
        return new ForEachStateBuilder(inputExpr);
    }

    protected final S state;
    protected final Collection<FunctionBuilder> functionDefinitions = new ArrayList<>();

    Collection<FunctionBuilder> getFunctions() {
        return functionDefinitions;
    }

    protected StateBuilder(S state) {
        this.state = state;
    }

    public T name(String name) {
        state.withName(name);
        return (T) this;
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
        return ensureName(state);
    }

    public S build(End end) {
        ensureName(state).withEnd(end);
        return state;
    }

    private static int counter;

    protected static <T extends DefaultState> T ensureName(T state) {
        if (state.getName() == null) {
            state.setName(state.getType() + "_" + counter++);
        }
        return state;
    }
}
