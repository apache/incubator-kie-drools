/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.serverless.workflow.utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jbpm.process.core.ContextContainer;
import org.jbpm.process.core.context.variable.Variable;
import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.process.instance.ContextInstanceContainer;
import org.jbpm.process.instance.ContextableInstance;
import org.jbpm.process.instance.context.variable.VariableScopeInstance;
import org.jbpm.ruleflow.core.Metadata;
import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.core.node.ForEachNode;
import org.jbpm.workflow.instance.WorkflowProcessInstance;
import org.kie.api.runtime.process.NodeInstanceContainer;
import org.kie.kogito.internal.process.runtime.KogitoNodeInstance;
import org.kie.kogito.internal.process.runtime.KogitoProcessContext;
import org.kie.kogito.jackson.utils.JsonObjectUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class JsonNodeContext implements AutoCloseable {

    private final JsonNode jsonNode;
    private final Set<String> keys;

    public static Stream<Variable> getEvalVariables(Node node) {
        if (node instanceof ForEachNode) {
            node = ((ForEachNode) node).getCompositeNode();
        }
        if (node instanceof ContextContainer) {
            return getEvalVariables((ContextContainer) node);
        }
        return Stream.empty();
    }

    private static Stream<Variable> getEvalVariables(ContextableInstance containerInstance) {
        return containerInstance instanceof ContextInstanceContainer ? getEvalVariables(((ContextInstanceContainer) containerInstance).getContextContainer()) : Stream.empty();
    }

    private static Stream<Variable> getEvalVariables(ContextContainer container) {
        VariableScope variableScope = (VariableScope) container.getDefaultContext(VariableScope.VARIABLE_SCOPE);
        return variableScope.getVariables().stream().filter(v -> v.getMetaData(Metadata.EVAL_VARIABLE) != null);
    }

    public static JsonNodeContext from(JsonNode jsonNode, KogitoProcessContext context) {
        Map<String, JsonNode> map = new HashMap<>();
        if (jsonNode.isObject()) {
            ObjectNode objectNode = (ObjectNode) jsonNode;
            addVariablesFromContext(objectNode, context, map);
        }
        return new JsonNodeContext(jsonNode, map.keySet());
    }

    public JsonNode getNode() {
        return jsonNode;
    }

    private JsonNodeContext(JsonNode jsonNode, Set<String> keys) {
        this.jsonNode = jsonNode;
        this.keys = keys;
    }

    private static void addVariablesFromContext(ObjectNode jsonNode, KogitoProcessContext processInfo, Map<String, JsonNode> variables) {
        KogitoNodeInstance nodeInstance = processInfo.getNodeInstance();
        if (nodeInstance != null) {
            NodeInstanceContainer container = nodeInstance instanceof NodeInstanceContainer ? (NodeInstanceContainer) nodeInstance : nodeInstance.getNodeInstanceContainer();
            while (container instanceof ContextableInstance) {
                getVariablesFromContext(jsonNode, (ContextableInstance) container, variables);
                container = container instanceof KogitoNodeInstance ? ((KogitoNodeInstance) container).getNodeInstanceContainer() : null;
            }
        }
        variables.forEach(jsonNode::set);
    }

    private static void getVariablesFromContext(ObjectNode jsonNode, ContextableInstance node, Map<String, JsonNode> variables) {
        VariableScopeInstance variableScope = (VariableScopeInstance) node.getContextInstance(VariableScope.VARIABLE_SCOPE);
        if (variableScope != null) {
            Collection<String> evalVariables = getEvalVariables(node).map(Variable::getName).collect(Collectors.toList());
            for (Entry<String, Object> e : variableScope.getVariables().entrySet()) {
                if (evalVariables.contains(e.getKey()) || node instanceof WorkflowProcessInstance && !Objects.equals(jsonNode, e.getValue())) {
                    variables.putIfAbsent(e.getKey(), JsonObjectUtils.fromValue(e.getValue()));
                }
            }
        }
    }

    @Override
    public void close() {
        if (!keys.isEmpty()) {
            keys.forEach(((ObjectNode) jsonNode)::remove);
        }
    }
}
