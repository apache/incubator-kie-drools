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
package org.kie.kogito.serverless.workflow.utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
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
import org.kie.kogito.serverless.workflow.SWFConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;

public class VariablesHelper {

    private static final Set<String> PREDEFINED_KEYS = Set.of(SWFConstants.DEFAULT_WORKFLOW_VAR, SWFConstants.INPUT_WORKFLOW_VAR);
    private static final Logger logger = LoggerFactory.getLogger(VariablesHelper.class);

    private VariablesHelper() {
    }

    public static Map<String, JsonNode> getAdditionalVariables(KogitoProcessContext context) {
        Map<String, JsonNode> variables = new HashMap<>();
        KogitoNodeInstance nodeInstance = context.getNodeInstance();
        if (nodeInstance != null) {
            NodeInstanceContainer container = nodeInstance instanceof NodeInstanceContainer ? (NodeInstanceContainer) nodeInstance : nodeInstance.getNodeInstanceContainer();
            while (container instanceof ContextableInstance) {
                addVariablesFromContext((ContextableInstance) container, variables);
                container = container instanceof KogitoNodeInstance ? ((KogitoNodeInstance) container).getNodeInstanceContainer() : null;
            }
        }
        logger.debug("Additional variables for expression evaluation are {}", variables);
        return variables;
    }

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
        return variableScope.getVariables().stream().filter(VariablesHelper::isEvalVariable);
    }

    private static boolean isEvalVariable(Variable v) {
        Object isEval = v.getMetaData(Metadata.EVAL_VARIABLE);
        return isEval instanceof Boolean ? ((Boolean) isEval).booleanValue() : false;
    }

    private static void addVariablesFromContext(ContextableInstance node, Map<String, JsonNode> variables) {
        VariableScopeInstance variableScope = (VariableScopeInstance) node.getContextInstance(VariableScope.VARIABLE_SCOPE);
        if (variableScope != null) {
            Collection<String> evalVariables = getEvalVariables(node).map(Variable::getName).collect(Collectors.toList());
            for (Entry<String, Object> e : variableScope.getVariables().entrySet()) {
                if (evalVariables.contains(e.getKey()) || node instanceof WorkflowProcessInstance && !PREDEFINED_KEYS.contains(e.getKey())) {
                    variables.putIfAbsent(e.getKey(), JsonObjectUtils.fromValue(e.getValue()));
                }
            }
        }
    }
}
