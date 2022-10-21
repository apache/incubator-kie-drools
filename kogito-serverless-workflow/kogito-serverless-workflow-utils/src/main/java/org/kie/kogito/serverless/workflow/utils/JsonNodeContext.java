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

import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.jbpm.process.core.ContextResolver;
import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.process.instance.ContextableInstance;
import org.jbpm.process.instance.context.variable.VariableScopeInstance;
import org.jbpm.ruleflow.core.Metadata;
import org.kie.api.definition.process.Node;
import org.kie.api.runtime.process.NodeInstanceContainer;
import org.kie.kogito.internal.process.runtime.KogitoNodeInstance;
import org.kie.kogito.internal.process.runtime.KogitoProcessContext;
import org.kie.kogito.jackson.utils.JsonObjectUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class JsonNodeContext implements AutoCloseable {

    private final JsonNode jsonNode;
    private final Set<String> keys;

    public static JsonNodeContext from(JsonNode jsonNode, KogitoProcessContext context) {
        Map<String, JsonNode> map = Collections.emptyMap();
        if (jsonNode.isObject()) {
            ObjectNode objectNode = (ObjectNode) jsonNode;
            map = addVariablesFromContext(context);
            map.forEach(objectNode::set);
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

    private static Map<String, JsonNode> addVariablesFromContext(KogitoProcessContext processInfo) {
        KogitoNodeInstance nodeInstance = processInfo.getNodeInstance();
        if (nodeInstance instanceof ContextableInstance) {
            return getVariablesFromContext((ContextableInstance) nodeInstance);
        } else if (nodeInstance != null) {
            NodeInstanceContainer container = nodeInstance.getNodeInstanceContainer();
            if (container instanceof ContextableInstance && container instanceof KogitoNodeInstance) {
                return getVariablesFromContext((ContextableInstance) container);
            }
        }
        return Collections.emptyMap();

    }

    private static boolean isEvalVariable(String varName, KogitoNodeInstance nodeInstance) {
        Node node = nodeInstance.getNode();
        VariableScope scope = (VariableScope) ((ContextResolver) node).resolveContext(VariableScope.VARIABLE_SCOPE, varName);
        return scope.getVariables().stream().filter(v -> v.getName().equals(varName)).findAny().orElseThrow().getMetaData(Metadata.EVAL_VARIABLE) != null;
    }

    private static Map<String, JsonNode> getVariablesFromContext(ContextableInstance node) {
        VariableScopeInstance variableScope = (VariableScopeInstance) node.getContextInstance(VariableScope.VARIABLE_SCOPE);
        return variableScope.getVariables().entrySet().stream().filter(e -> isEvalVariable(e.getKey(), (KogitoNodeInstance) node))
                .collect(Collectors.toMap(Entry::getKey, entry -> JsonObjectUtils.fromValue(entry.getValue())));

    }

    @Override
    public void close() {
        if (!keys.isEmpty()) {
            keys.forEach(((ObjectNode) jsonNode)::remove);
        }
    }
}
