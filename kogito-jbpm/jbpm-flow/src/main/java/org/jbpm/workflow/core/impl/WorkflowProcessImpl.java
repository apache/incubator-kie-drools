/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.workflow.core.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.regex.Matcher;

import org.jbpm.process.core.correlation.CorrelationManager;
import org.jbpm.process.core.impl.ProcessImpl;
import org.jbpm.process.instance.ProcessInstance;
import org.jbpm.util.PatternConstants;
import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.core.WorkflowInputModelValidator;
import org.jbpm.workflow.core.WorkflowProcess;
import org.jbpm.workflow.core.node.StartNode;
import org.jbpm.workflow.instance.WorkflowProcessInstance;
import org.jbpm.workflow.instance.impl.MVELProcessHelper;
import org.jbpm.workflow.instance.impl.ProcessInstanceResolverFactory;
import org.kie.api.definition.process.NodeContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default implementation of a RuleFlow process.
 *
 */
public class WorkflowProcessImpl extends ProcessImpl implements WorkflowProcess, org.jbpm.workflow.core.NodeContainer {

    private static final long serialVersionUID = 510l;
    private static final Logger logger = LoggerFactory.getLogger(WorkflowProcessImpl.class);

    private boolean autoComplete = false;
    private boolean dynamic = false;
    private WorkflowInputModelValidator validator;
    private org.jbpm.workflow.core.NodeContainer nodeContainer;

    private transient BiFunction<String, ProcessInstance, String> expressionEvaluator = (expression, p) -> {

        String evaluatedValue = expression;
        Map<String, String> replacements = new HashMap<>();
        Matcher matcher = PatternConstants.PARAMETER_MATCHER.matcher(evaluatedValue);
        while (matcher.find()) {
            String paramName = matcher.group(1);
            if (replacements.get(paramName) == null) {
                try {
                    String value = (String) MVELProcessHelper.evaluator()
                            .eval(paramName, new ProcessInstanceResolverFactory(((WorkflowProcessInstance) p)));
                    replacements.put(paramName, value);
                } catch (Throwable t) {
                    logger.error("Could not resolve, parameter {} while evaluating expression {}", paramName, expression, t);
                }
            }
        }
        for (Map.Entry<String, String> replacement : replacements.entrySet()) {
            evaluatedValue = evaluatedValue.replace("#{" + replacement.getKey() + "}", replacement.getValue());
        }

        return evaluatedValue;

    };

    private CorrelationManager correlationManager = new CorrelationManager();

    public CorrelationManager getCorrelationManager() {
        return correlationManager;
    }

    public WorkflowProcessImpl() {
        nodeContainer = (org.jbpm.workflow.core.NodeContainer) createNodeContainer();
    }

    protected NodeContainer createNodeContainer() {
        return new NodeContainerImpl();
    }

    @Override
    public org.kie.api.definition.process.Node[] getNodes() {
        return nodeContainer.getNodes();
    }

    @Override
    public org.kie.api.definition.process.Node getNode(final long id) {
        return nodeContainer.getNode(id);
    }

    @Override
    public org.kie.api.definition.process.Node getNodeByUniqueId(String s) {
        throw new UnsupportedOperationException();
    }

    @Override
    public org.kie.api.definition.process.Node internalGetNode(long id) {
        try {
            return getNode(id);
        } catch (IllegalArgumentException e) {
            if (dynamic) {
                return null;
            } else {
                throw e;
            }
        }
    }

    @Override
    public void removeNode(final org.kie.api.definition.process.Node node) {
        nodeContainer.removeNode(node);
        ((Node) node).setParentContainer(null);
    }

    @Override
    public void addNode(final org.kie.api.definition.process.Node node) {
        nodeContainer.addNode(node);
        ((Node) node).setParentContainer(this);
    }

    @Override
    public boolean isAutoComplete() {
        return autoComplete;
    }

    public void setAutoComplete(boolean autoComplete) {
        this.autoComplete = autoComplete;
    }

    @Override
    public boolean isDynamic() {
        return dynamic;
    }

    public void setDynamic(boolean dynamic) {
        this.dynamic = dynamic;
    }

    @Override
    public Integer getProcessType() {
        if (dynamic) {
            return CASE_TYPE;
        }
        return PROCESS_TYPE;
    }

    @Override
    public List<org.kie.api.definition.process.Node> getNodesRecursively() {
        List<org.kie.api.definition.process.Node> nodes = new ArrayList<>();

        processNodeContainer(nodeContainer, nodes);

        return nodes;
    }

    protected void processNodeContainer(org.jbpm.workflow.core.NodeContainer nodeContainer, List<org.kie.api.definition.process.Node> nodes) {

        for (org.kie.api.definition.process.Node node : nodeContainer.getNodes()) {
            nodes.add(node);
            if (node instanceof org.jbpm.workflow.core.NodeContainer) {
                processNodeContainer((org.jbpm.workflow.core.NodeContainer) node, nodes);
            }
        }
    }

    protected org.kie.api.definition.process.Node getContainerNode(org.kie.api.definition.process.Node currentNode, org.jbpm.workflow.core.NodeContainer nodeContainer, long nodeId) {
        for (org.kie.api.definition.process.Node node : nodeContainer.getNodes()) {
            if (nodeId == node.getId()) {
                return currentNode;
            } else {
                if (node instanceof org.jbpm.workflow.core.NodeContainer) {
                    return getContainerNode(node, (org.jbpm.workflow.core.NodeContainer) node, nodeId);
                }
            }
        }
        return null;
    }

    public org.kie.api.definition.process.Node getParentNode(long nodeId) {
        return getContainerNode(null, nodeContainer, nodeId);
    }

    public List<StartNode> getTimerStart() {
        org.kie.api.definition.process.Node[] nodes = getNodes();

        List<StartNode> timerStartNodes = new ArrayList<>();
        for (int i = 0; i < nodes.length; i++) {
            if (nodes[i] instanceof StartNode && ((StartNode) nodes[i]).getTimer() != null) {
                timerStartNodes.add((StartNode) nodes[i]);
            }
        }

        return timerStartNodes;
    }

    @Override
    public void setExpressionEvaluator(BiFunction<String, ProcessInstance, String> expressionEvaluator) {
        this.expressionEvaluator = expressionEvaluator;
    }

    @Override
    public String evaluateExpression(String metaData, ProcessInstance processInstance) {
        return this.expressionEvaluator.apply(metaData, processInstance);
    }

    @Override
    public Optional<WorkflowInputModelValidator> getValidator() {
        return Optional.ofNullable(validator);
    }

    @Override
    public void setValidator(WorkflowInputModelValidator validator) {
        this.validator = validator;
    }
}
