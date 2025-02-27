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

import java.util.Iterator;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.kie.kogito.internal.process.workitem.KogitoWorkItem;
import org.kie.kogito.jackson.utils.JsonObjectUtils;
import org.kie.kogito.serverless.workflow.SWFConstants;
import org.kie.kogito.serverless.workflow.WorkflowWorkItemHandler;
import org.kie.kogito.serverless.workflow.functions.FunctionDefinitionEx;
import org.kie.kogito.serverless.workflow.functions.TriFunction;

import com.fasterxml.jackson.databind.JsonNode;

import static org.kie.kogito.serverless.workflow.SWFConstants.CONTENT_DATA;

public class StaticFunctionWorkItemHandler extends WorkflowWorkItemHandler {

    private final FunctionDefinitionEx functionDef;

    public StaticFunctionWorkItemHandler(FunctionDefinitionEx functionDef) {
        this.functionDef = functionDef;
    }

    @Override
    public String getName() {
        return functionDef.getName();
    }

    @Override
    protected Object internalExecute(KogitoWorkItem workItem, Map<String, Object> parameters) {
        if (functionDef.getFunction() != null) {
            return internalExecute(functionDef.getFunction(), workItem, parameters);
        } else if (functionDef.getBiFunction() != null) {
            return internalExecute(functionDef.getBiFunction(), parameters);
        } else if (functionDef.getTriFunction() != null) {
            return internalExecute(functionDef.getTriFunction(), parameters);
        }
        throw new IllegalStateException("No function set");
    }

    private Object internalExecute(TriFunction triFunction, Map<String, Object> parameters) {
        checkParamsSize(parameters, 3);
        Iterator<Object> iter = parameters.values().iterator();
        return triFunction.apply(iter.next(), iter.next(), iter.next());
    }

    private Object internalExecute(BiFunction biFunction, Map<String, Object> parameters) {
        checkParamsSize(parameters, 2);
        Iterator<Object> iter = parameters.values().iterator();
        return biFunction.apply(iter.next(), iter.next());
    }

    private void checkParamsSize(Map<String, Object> parameters, int size) {
        if (parameters.size() != size) {
            throw new IllegalArgumentException("Wrong number of arguments. The function expects " + size);
        }
    }

    private Object internalExecute(Function function, KogitoWorkItem workItem, Map<String, Object> parameters) {
        int size = parameters.size();
        if (size == 0) {
            return function.apply(JsonObjectUtils.toJavaValue((JsonNode) workItem.getParameter(SWFConstants.MODEL_WORKFLOW_VAR)));
        } else if (size == 1 && parameters.containsKey(CONTENT_DATA)) {
            return function.apply(parameters.get(CONTENT_DATA));
        } else {
            return function.apply(parameters);
        }
    }
}
