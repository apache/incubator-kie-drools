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
package org.kie.kogito.serverless.workflow.executor;

import java.util.Map;
import java.util.function.Function;

import org.kie.kogito.internal.process.workitem.KogitoWorkItem;
import org.kie.kogito.jackson.utils.JsonObjectUtils;
import org.kie.kogito.serverless.workflow.SWFConstants;
import org.kie.kogito.serverless.workflow.WorkflowWorkItemHandler;

import com.fasterxml.jackson.databind.JsonNode;

import static org.kie.kogito.serverless.workflow.SWFConstants.CONTENT_DATA;

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
        int size = parameters.size();
        if (size == 0) {
            return function.apply((V) JsonObjectUtils.toJavaValue((JsonNode) workItem.getParameter(SWFConstants.MODEL_WORKFLOW_VAR)));
        } else if (size == 1 && parameters.containsKey(CONTENT_DATA)) {
            return function.apply((V) parameters.get(CONTENT_DATA));
        } else {
            return function.apply((V) parameters);
        }
    }
}
