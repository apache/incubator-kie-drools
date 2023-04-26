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

import java.util.Optional;

import org.kie.kogito.jackson.utils.JsonObjectUtils;
import org.kie.kogito.process.Process;
import org.kie.kogito.serverless.workflow.actions.WorkflowLogLevel;
import org.kie.kogito.serverless.workflow.models.JsonNodeModel;
import org.kie.kogito.serverless.workflow.parser.types.SysOutTypeHandler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;

import io.serverlessworkflow.api.actions.Action;
import io.serverlessworkflow.api.filters.ActionDataFilter;
import io.serverlessworkflow.api.functions.FunctionRef;
import io.serverlessworkflow.api.functions.SubFlowRef;

import static org.kie.kogito.serverless.workflow.fluent.WorkflowBuilder.jsonObject;

public class ActionBuilder {

    private Action action;
    private Optional<FunctionBuilder> functionDefinition = Optional.empty();

    final Optional<FunctionBuilder> getFunction() {
        return functionDefinition;
    }

    public static ActionBuilder call(String functionName) {
        return call(functionName, NullNode.instance);
    }

    public static ActionBuilder call(FunctionBuilder functionBuilder) {
        return call(functionBuilder, NullNode.instance);
    }

    public static ActionBuilder call(String functionName, Object args) {
        return call(functionName, JsonObjectUtils.fromValue(args));
    }

    public static ActionBuilder call(FunctionBuilder functionBuilder, JsonNode args) {
        ActionBuilder actionBuilder = call(functionBuilder.getName(), args);
        actionBuilder.functionDefinition = Optional.of(functionBuilder);
        return actionBuilder;
    }

    public static ActionBuilder call(FunctionBuilder functionBuilder, Object args) {
        return call(functionBuilder, JsonObjectUtils.fromValue(args));
    }

    public static ActionBuilder call(String functionName, JsonNode args) {
        return new ActionBuilder(new Action().withFunctionRef(new FunctionRef().withRefName(functionName).withArguments(args)));
    }

    public static ActionBuilder log(String functionName, String message) {
        return call(functionName, logArgs(message));
    }

    public static ActionBuilder log(WorkflowLogLevel logLevel, String message) {
        return call(FunctionBuilder.log("log-" + logLevel, logLevel), logArgs(message));
    }

    private static JsonNode logArgs(String message) {
        return jsonObject().put(SysOutTypeHandler.SYSOUT_TYPE_PARAM, message);
    }

    public static ActionBuilder subprocess(Process<JsonNodeModel> subprocess) {
        return new ActionBuilder(new Action().withSubFlowRef(new SubFlowRef().withWorkflowId(subprocess.id())));
    }

    protected ActionBuilder(Action action) {
        this.action = action;
    }

    public ActionBuilder name(String name) {
        action.withName(name);
        return this;
    }

    public ActionBuilder condition(String expr) {
        action.withCondition(expr);
        return this;
    }

    public Action build() {
        return action;
    }

    private ActionDataFilter getFilter() {
        ActionDataFilter actionFilter = null;
        if (action != null) {
            actionFilter = action.getActionDataFilter();
            if (actionFilter == null) {
                actionFilter = new ActionDataFilter();
                action.withActionDataFilter(actionFilter);
            }
        }
        return actionFilter;
    }

    public ActionBuilder noResult() {
        ActionDataFilter filter = getFilter();
        if (filter != null) {
            filter.withUseResults(false);
        }
        return this;
    }

    public ActionBuilder inputFilter(String expr) {
        ActionDataFilter filter = getFilter();
        if (filter != null) {
            filter.withFromStateData(expr);
        }
        return this;
    }

    public ActionBuilder resultFilter(String expr) {
        ActionDataFilter filter = getFilter();
        if (filter != null) {
            filter.withResults(expr);
        }
        return this;
    }

    public ActionBuilder outputFilter(String expr) {
        ActionDataFilter filter = getFilter();
        if (filter != null) {
            filter.withToStateData(expr);
        }
        return this;
    }

}
