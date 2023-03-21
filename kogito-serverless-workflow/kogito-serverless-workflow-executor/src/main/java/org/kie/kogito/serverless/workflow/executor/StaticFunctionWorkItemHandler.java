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
package org.kie.kogito.serverless.workflow.executor;

import java.util.Map;
import java.util.function.Function;

import org.kie.kogito.internal.process.runtime.KogitoWorkItem;
import org.kie.kogito.serverless.workflow.WorkflowWorkItemHandler;

import static org.kogito.workitem.rest.RestWorkItemHandler.CONTENT_DATA;

public class StaticFunctionWorkItemHandler<V, T> extends WorkflowWorkItemHandler {

    private final String name;
    private final Function<V, T> function;

    public StaticFunctionWorkItemHandler(String name, Function<V, T> function) {
        this.name = name;
        this.function = function;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    protected Object internalExecute(KogitoWorkItem workItem, Map<String, Object> parameters) {
        return parameters.size() == 1 && parameters.containsKey(CONTENT_DATA) ? function.apply((V) parameters.get(CONTENT_DATA))
                : function.apply((V) parameters);
    }
}
