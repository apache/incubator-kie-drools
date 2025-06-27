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
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import org.kie.kogito.event.cloudevents.CloudEventExtensionConstants;
import org.kie.kogito.jackson.utils.JsonObjectUtils;
import org.kie.kogito.jackson.utils.ObjectMapperFactory;
import org.kie.kogito.process.Process;
import org.kie.kogito.serverless.workflow.SWFConstants;
import org.kie.kogito.serverless.workflow.actions.WorkflowLogLevel;
import org.kie.kogito.serverless.workflow.models.JsonNodeModel;
import org.kie.kogito.serverless.workflow.parser.types.SysOutTypeHandler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.serverlessworkflow.api.actions.Action;
import io.serverlessworkflow.api.events.EventRef;
import io.serverlessworkflow.api.filters.ActionDataFilter;
import io.serverlessworkflow.api.functions.FunctionDefinition.Type;
import io.serverlessworkflow.api.functions.FunctionRef;
import io.serverlessworkflow.api.functions.SubFlowRef;
import io.serverlessworkflow.api.sleep.Sleep;

import static org.kie.kogito.serverless.workflow.fluent.WorkflowBuilder.jsonObject;
import static org.kie.kogito.serverless.workflow.parser.FunctionTypeHandlerFactory.CUSTOM_TYPE_SEPARATOR;

public class ActionBuilder {

    private Action action;
    private Optional<FunctionBuilder> functionDefinition = Optional.empty();
    private Optional<EventDefBuilder> eventDefinition = Optional.empty();

    public enum ScriptType {
        PYTHON,
        JAVA
    }

    final Optional<FunctionBuilder> getFunction() {
        return functionDefinition;
    }

    final Optional<EventDefBuilder> getEvent() {
        return eventDefinition;
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

    public static ActionBuilder trigger(EventDefBuilder builder, JsonNode data) {
        return trigger(builder, data, Collections.emptyMap());
    }

    public static ActionBuilder trigger(EventDefBuilder builder, JsonNode data, String procRefId) {
        return trigger(builder, data, Map.of(CloudEventExtensionConstants.PROCESS_REFERENCE_ID, procRefId));
    }

    public static ActionBuilder trigger(EventDefBuilder builder, JsonNode data, Map<String, String> contextAttributes) {
        ActionBuilder actionBuilder = new ActionBuilder(new Action().withEventRef(new EventRef().withContextAttributes(contextAttributes).withData(data).withTriggerEventRef(builder.getName())));
        actionBuilder.eventDefinition = Optional.of(builder);
        return actionBuilder;
    }

    public static ActionBuilder trigger(EventDefBuilder builder, String data) {
        return trigger(builder, JsonObjectUtils.fromString(data));
    }

    public static ActionBuilder call(FunctionBuilder functionBuilder, JsonNode args) {
        ActionBuilder actionBuilder = call(functionBuilder.getName(), args);
        actionBuilder.functionDefinition = Optional.of(functionBuilder);
        return actionBuilder;
    }

    public static ActionBuilder script(String source, ScriptType type) {
        return script(source, type, jsonObject());
    }

    public static ActionBuilder script(String source, ScriptType type, ObjectNode args) {
        return call(FunctionBuilder.def(type.toString(), Type.CUSTOM, SWFConstants.SCRIPT + CUSTOM_TYPE_SEPARATOR + type.toString()), args.put(SWFConstants.SCRIPT, source));
    }

    public static ActionBuilder call(FunctionBuilder functionBuilder, Object args) {
        return call(functionBuilder, JsonObjectUtils.fromValue(args));
    }

    public static ActionBuilder call(FunctionBuilder functionBuilder, Object first, Object second, Object... extras) {
        ObjectNode node = ObjectMapperFactory.get().createObjectNode();
        node.set("arg1", JsonObjectUtils.fromValue(first));
        node.set("arg2", JsonObjectUtils.fromValue(second));
        int i = 3;
        for (Object extra : extras) {
            node.set("arg" + i++, JsonObjectUtils.fromValue(extra));
        }
        return call(functionBuilder, node);
    }

    public static ActionBuilder call(String functionName, JsonNode args) {
        return new ActionBuilder(new Action().withFunctionRef(new FunctionRef().withRefName(functionName).withArguments(args)));
    }

    /**
     * @deprecated Replaced by {@link #log(WorkflowLogLevel)}
     */
    @Deprecated
    public static ActionBuilder log(String functionName, String message) {
        return call(functionName, logArgs(message));
    }

    public static ActionBuilder log(WorkflowLogLevel logLevel, String message) {
        return call(FunctionBuilder.log(logLevel), logArgs(message));
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

    public ActionBuilder sleepBefore(Duration duration) {
        action.withSleep(new Sleep().withBefore(duration.toString()));
        return this;
    }

    public ActionBuilder sleepAfter(Duration duration) {
        action.withSleep(new Sleep().withAfter(duration.toString()));
        return this;
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
        ActionDataFilter actionFilter = action.getActionDataFilter();
        if (actionFilter == null) {
            actionFilter = new ActionDataFilter();
            action.withActionDataFilter(actionFilter);
        }

        return actionFilter;
    }

    public ActionBuilder noResult() {
        getFilter().withUseResults(false);
        return this;
    }

    public ActionBuilder inputFilter(String expr) {
        getFilter().withFromStateData(expr);
        return this;
    }

    public ActionBuilder resultFilter(String expr) {
        getFilter().withResults(expr);
        return this;
    }

    public ActionBuilder outputFilter(String expr) {
        getFilter().withToStateData(expr);
        return this;
    }
}
