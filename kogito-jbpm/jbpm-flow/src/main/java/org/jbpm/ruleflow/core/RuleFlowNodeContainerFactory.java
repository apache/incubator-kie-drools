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
package org.jbpm.ruleflow.core;

import org.jbpm.process.core.Context;
import org.jbpm.process.core.ContextContainer;
import org.jbpm.process.core.context.exception.ActionExceptionHandler;
import org.jbpm.process.core.context.exception.CompensationScope;
import org.jbpm.process.core.context.exception.ExceptionHandler;
import org.jbpm.process.core.context.exception.ExceptionScope;
import org.jbpm.process.core.datatype.DataType;
import org.jbpm.process.instance.impl.actions.SignalProcessInstanceAction;
import org.jbpm.ruleflow.core.factory.ActionNodeFactory;
import org.jbpm.ruleflow.core.factory.BoundaryEventNodeFactory;
import org.jbpm.ruleflow.core.factory.CatchLinkNodeFactory;
import org.jbpm.ruleflow.core.factory.CompositeContextNodeFactory;
import org.jbpm.ruleflow.core.factory.DynamicNodeFactory;
import org.jbpm.ruleflow.core.factory.EndNodeFactory;
import org.jbpm.ruleflow.core.factory.EventNodeFactory;
import org.jbpm.ruleflow.core.factory.EventSubProcessNodeFactory;
import org.jbpm.ruleflow.core.factory.ExtendedNodeFactory;
import org.jbpm.ruleflow.core.factory.FaultNodeFactory;
import org.jbpm.ruleflow.core.factory.ForEachNodeFactory;
import org.jbpm.ruleflow.core.factory.HumanTaskNodeFactory;
import org.jbpm.ruleflow.core.factory.JoinFactory;
import org.jbpm.ruleflow.core.factory.MilestoneNodeFactory;
import org.jbpm.ruleflow.core.factory.NodeFactory;
import org.jbpm.ruleflow.core.factory.RuleSetNodeFactory;
import org.jbpm.ruleflow.core.factory.SplitFactory;
import org.jbpm.ruleflow.core.factory.StartNodeFactory;
import org.jbpm.ruleflow.core.factory.StateNodeFactory;
import org.jbpm.ruleflow.core.factory.SubProcessNodeFactory;
import org.jbpm.ruleflow.core.factory.ThrowLinkNodeFactory;
import org.jbpm.ruleflow.core.factory.TimerNodeFactory;
import org.jbpm.ruleflow.core.factory.WorkItemNodeFactory;
import org.jbpm.ruleflow.core.factory.provider.NodeFactoryProviderService;
import org.jbpm.workflow.core.Connection;
import org.jbpm.workflow.core.NodeContainer;
import org.jbpm.workflow.core.impl.ConnectionImpl;
import org.jbpm.workflow.core.impl.DroolsConsequenceAction;
import org.jbpm.workflow.core.node.ActionNode;
import org.jbpm.workflow.core.node.BoundaryEventNode;
import org.jbpm.workflow.core.node.CatchLinkNode;
import org.jbpm.workflow.core.node.CompositeContextNode;
import org.jbpm.workflow.core.node.DynamicNode;
import org.jbpm.workflow.core.node.EndNode;
import org.jbpm.workflow.core.node.EventNode;
import org.jbpm.workflow.core.node.EventSubProcessNode;
import org.jbpm.workflow.core.node.FaultNode;
import org.jbpm.workflow.core.node.ForEachNode;
import org.jbpm.workflow.core.node.HumanTaskNode;
import org.jbpm.workflow.core.node.Join;
import org.jbpm.workflow.core.node.MilestoneNode;
import org.jbpm.workflow.core.node.RuleSetNode;
import org.jbpm.workflow.core.node.Split;
import org.jbpm.workflow.core.node.StartNode;
import org.jbpm.workflow.core.node.StateNode;
import org.jbpm.workflow.core.node.SubProcessNode;
import org.jbpm.workflow.core.node.ThrowLinkNode;
import org.jbpm.workflow.core.node.TimerNode;
import org.jbpm.workflow.core.node.WorkItemNode;
import org.kie.api.definition.process.Node;
import org.kie.api.definition.process.WorkflowElementIdentifier;

import static org.jbpm.ruleflow.core.Metadata.ASSOCIATION;
import static org.jbpm.ruleflow.core.Metadata.HIDDEN;
import static org.jbpm.ruleflow.core.Metadata.UNIQUE_ID;
import static org.jbpm.workflow.core.Node.CONNECTION_DEFAULT_TYPE;

public abstract class RuleFlowNodeContainerFactory<T extends RuleFlowNodeContainerFactory<T, P>, P extends RuleFlowNodeContainerFactory<P, ?>> extends ExtendedNodeFactory<T, P> {

    public static final String METHOD_CONNECTION = "connection";
    public static final String METHOD_ASSOCIATION = "association";

    private NodeFactoryProviderService provider;

    public RuleFlowNodeContainerFactory(P nodeContainerFactory, NodeContainer nodeContainer, org.jbpm.workflow.core.Node node, WorkflowElementIdentifier id) {
        super(nodeContainerFactory, nodeContainer, node, id);
        provider = new NodeFactoryProviderService();
    }

    protected abstract NodeContainer getNodeContainer();

    public <R extends NodeFactory<R, T>> R newNode(Class<?> node, WorkflowElementIdentifier id) {
        return provider.newNodeFactory(node, (T) this, getNodeContainer(), id);
    }

    public StartNodeFactory<T> startNode(WorkflowElementIdentifier id) {
        return newNode(StartNode.class, id);
    }

    public EndNodeFactory<T> endNode(WorkflowElementIdentifier id) {
        return newNode(EndNode.class, id);
    }

    public CatchLinkNodeFactory<T> catchLinkNode(WorkflowElementIdentifier id) {
        return newNode(CatchLinkNode.class, id);
    }

    public ThrowLinkNodeFactory<T> throwLinkNode(WorkflowElementIdentifier id) {
        return newNode(ThrowLinkNode.class, id);
    }

    public ActionNodeFactory<T> actionNode(WorkflowElementIdentifier id) {
        return newNode(ActionNode.class, id);
    }

    public MilestoneNodeFactory<T> milestoneNode(WorkflowElementIdentifier id) {
        return newNode(MilestoneNode.class, id);
    }

    public TimerNodeFactory<T> timerNode(WorkflowElementIdentifier id) {
        return newNode(TimerNode.class, id);
    }

    public HumanTaskNodeFactory<T> humanTaskNode(WorkflowElementIdentifier id) {
        return newNode(HumanTaskNode.class, id);
    }

    public SubProcessNodeFactory<T> subProcessNode(WorkflowElementIdentifier id) {
        return newNode(SubProcessNode.class, id);
    }

    public SplitFactory<T> splitNode(WorkflowElementIdentifier id) {
        return newNode(Split.class, id);
    }

    public JoinFactory<T> joinNode(WorkflowElementIdentifier id) {
        return newNode(Join.class, id);
    }

    public RuleSetNodeFactory<T> ruleSetNode(WorkflowElementIdentifier id) {
        return newNode(RuleSetNode.class, id);
    }

    public FaultNodeFactory<T> faultNode(WorkflowElementIdentifier id) {
        return newNode(FaultNode.class, id);
    }

    public EventNodeFactory<T> eventNode(WorkflowElementIdentifier id) {
        return newNode(EventNode.class, id);
    }

    public BoundaryEventNodeFactory<T> boundaryEventNode(WorkflowElementIdentifier id) {
        return newNode(BoundaryEventNode.class, id);
    }

    public CompositeContextNodeFactory<T> compositeContextNode(WorkflowElementIdentifier id) {
        return newNode(CompositeContextNode.class, id);
    }

    public ForEachNodeFactory<T> forEachNode(WorkflowElementIdentifier id) {
        return newNode(ForEachNode.class, id);
    }

    public DynamicNodeFactory<T> dynamicNode(WorkflowElementIdentifier id) {
        return newNode(DynamicNode.class, id);
    }

    public WorkItemNodeFactory<T> workItemNode(WorkflowElementIdentifier id) {
        return newNode(WorkItemNode.class, id);
    }

    public EventSubProcessNodeFactory<T> eventSubProcessNode(WorkflowElementIdentifier id) {
        return newNode(EventSubProcessNode.class, id);
    }

    public StateNodeFactory<T> stateNode(WorkflowElementIdentifier id) {
        return newNode(StateNode.class, id);
    }

    public T connection(WorkflowElementIdentifier fromId, WorkflowElementIdentifier toId) {
        return connection(fromId, toId, fromId.toSanitizeString() + "_" + toId.toSanitizeString());
    }

    public T connection(WorkflowElementIdentifier fromId, WorkflowElementIdentifier toId, String uniqueId) {
        getConnection(fromId, toId, uniqueId);
        return (T) this;
    }

    public T association(WorkflowElementIdentifier fromId, WorkflowElementIdentifier toId, String uniqueId) {
        Connection connection = getConnection(fromId, toId, uniqueId);
        connection.setMetaData(ASSOCIATION, Boolean.TRUE);
        connection.setMetaData(HIDDEN, Boolean.TRUE);
        return (T) this;
    }

    private Connection getConnection(WorkflowElementIdentifier fromId, WorkflowElementIdentifier toId, String uniqueId) {
        Node from = ((NodeContainer) getNodeContainer()).getNode(fromId);
        Node to = ((NodeContainer) getNodeContainer()).getNode(toId);
        Connection connection = new ConnectionImpl(from, CONNECTION_DEFAULT_TYPE, to, CONNECTION_DEFAULT_TYPE);
        if (uniqueId != null) {
            connection.setMetaData(UNIQUE_ID, uniqueId);
        }
        return connection;
    }

    public T exceptionHandler(String exception, ExceptionHandler exceptionHandler) {
        getScope(ExceptionScope.EXCEPTION_SCOPE, ExceptionScope.class).setExceptionHandler(exception, exceptionHandler);
        return (T) this;
    }

    public T exceptionHandler(String exception, String dialect, String action) {
        ActionExceptionHandler exceptionHandler = new ActionExceptionHandler();
        exceptionHandler.setAction(new DroolsConsequenceAction(dialect, action));
        return exceptionHandler(exception, exceptionHandler);
    }

    public T exceptionHandler(String eventType, String exception) {
        return errorExceptionHandler(eventType, exception, null);
    }

    public T errorExceptionHandler(String signalType, String faultCode, String faultVariable) {
        ActionExceptionHandler exceptionHandler = new ActionExceptionHandler();
        DroolsConsequenceAction action = new DroolsConsequenceAction("java", "");
        action.setMetaData("Action", new SignalProcessInstanceAction(signalType, faultVariable, null, SignalProcessInstanceAction.PROCESS_INSTANCE_SCOPE));
        exceptionHandler.setAction(action);
        exceptionHandler.setFaultVariable(faultVariable);
        return exceptionHandler(faultCode, exceptionHandler);
    }

    public abstract T variable(String name, DataType type);

    public abstract T variable(String name, DataType type, Object value);

    public abstract T variable(String name, DataType type, String metaDataName, Object metaDataValue);

    public abstract T variable(String name, DataType type, Object value, String metaDataName, Object metaDataValue);

    private <S extends Context> S getScope(String scopeType, Class<S> scopeClass) {
        ContextContainer contextContainer = (ContextContainer) getNodeContainer();
        Context scope = contextContainer.getDefaultContext(scopeType);
        if (scope == null) {
            try {
                scope = scopeClass.getConstructor().newInstance();
            } catch (ReflectiveOperationException ex) {
                throw new IllegalStateException(ex);
            }
            contextContainer.addContext(scope);
            contextContainer.setDefaultContext(scope);
        }
        return scopeClass.cast(scope);
    }

    public RuleFlowNodeContainerFactory<T, P> addCompensationContext(String contextId) {
        if (getNodeContainer() instanceof ContextContainer) {
            CompensationScope compensationScope = new CompensationScope();
            ContextContainer contextNode = (ContextContainer) getNodeContainer();
            contextNode.addContext(compensationScope);
            contextNode.setDefaultContext(compensationScope);
            compensationScope.setContextContainerId(contextId);
        }
        return this;
    }
}
