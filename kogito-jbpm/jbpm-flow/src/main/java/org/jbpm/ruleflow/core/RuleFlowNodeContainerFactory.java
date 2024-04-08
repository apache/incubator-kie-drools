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
import org.jbpm.workflow.core.Connection;
import org.jbpm.workflow.core.NodeContainer;
import org.jbpm.workflow.core.impl.ConnectionImpl;
import org.jbpm.workflow.core.impl.DroolsConsequenceAction;
import org.kie.api.definition.process.Node;
import org.kie.api.definition.process.WorkflowElementIdentifier;

import static org.jbpm.ruleflow.core.Metadata.ASSOCIATION;
import static org.jbpm.ruleflow.core.Metadata.HIDDEN;
import static org.jbpm.ruleflow.core.Metadata.UNIQUE_ID;
import static org.jbpm.workflow.core.Node.CONNECTION_DEFAULT_TYPE;

public abstract class RuleFlowNodeContainerFactory<T extends RuleFlowNodeContainerFactory<T, P>, P extends RuleFlowNodeContainerFactory<P, ?>> extends NodeFactory<T, P> {

    public static final String METHOD_CONNECTION = "connection";
    public static final String METHOD_ASSOCIATION = "association";

    protected RuleFlowNodeContainerFactory(P nodeContainerFactory, NodeContainer nodeContainer, NodeContainer node, WorkflowElementIdentifier id) {
        super(nodeContainerFactory, nodeContainer, node, id);
    }

    public StartNodeFactory<T> startNode(WorkflowElementIdentifier id) {
        return new StartNodeFactory<>((T) this, (NodeContainer) node, id);
    }

    public EndNodeFactory<T> endNode(WorkflowElementIdentifier id) {
        return new EndNodeFactory<>((T) this, (NodeContainer) node, id);
    }

    public CatchLinkNodeFactory<T> catchLinkNode(WorkflowElementIdentifier id) {
        return new CatchLinkNodeFactory<>((T) this, (NodeContainer) node, id);
    }

    public ThrowLinkNodeFactory<T> throwLinkNode(WorkflowElementIdentifier id) {
        return new ThrowLinkNodeFactory<>((T) this, (NodeContainer) node, id);
    }

    public ActionNodeFactory<T> actionNode(WorkflowElementIdentifier id) {
        return new ActionNodeFactory<>((T) this, (NodeContainer) node, id);
    }

    public MilestoneNodeFactory<T> milestoneNode(WorkflowElementIdentifier id) {
        return new MilestoneNodeFactory<>((T) this, (NodeContainer) node, id);
    }

    public TimerNodeFactory<T> timerNode(WorkflowElementIdentifier id) {
        return new TimerNodeFactory<>((T) this, (NodeContainer) node, id);
    }

    public HumanTaskNodeFactory<T> humanTaskNode(WorkflowElementIdentifier id) {
        return new HumanTaskNodeFactory<>((T) this, (NodeContainer) node, id);
    }

    public SubProcessNodeFactory<T> subProcessNode(WorkflowElementIdentifier id) {
        return new SubProcessNodeFactory<>((T) this, (NodeContainer) node, id);
    }

    public SplitFactory<T> splitNode(WorkflowElementIdentifier id) {
        return new SplitFactory<>((T) this, (NodeContainer) node, id);
    }

    public JoinFactory<T> joinNode(WorkflowElementIdentifier id) {
        return new JoinFactory<>((T) this, (NodeContainer) node, id);
    }

    public RuleSetNodeFactory<T> ruleSetNode(WorkflowElementIdentifier id) {
        return new RuleSetNodeFactory<>((T) this, (NodeContainer) node, id);
    }

    public FaultNodeFactory<T> faultNode(WorkflowElementIdentifier id) {
        return new FaultNodeFactory<>((T) this, (NodeContainer) node, id);
    }

    public EventNodeFactory<T> eventNode(WorkflowElementIdentifier id) {
        return new EventNodeFactory<>((T) this, (NodeContainer) node, id);
    }

    public BoundaryEventNodeFactory<T> boundaryEventNode(WorkflowElementIdentifier id) {
        return new BoundaryEventNodeFactory<>((T) this, (NodeContainer) node, id);
    }

    public CompositeContextNodeFactory<T> compositeContextNode(WorkflowElementIdentifier id) {
        return new CompositeContextNodeFactory<>((T) this, (NodeContainer) node, id);
    }

    public ForEachNodeFactory<T> forEachNode(WorkflowElementIdentifier id) {
        return new ForEachNodeFactory<>((T) this, (NodeContainer) node, id);
    }

    public DynamicNodeFactory<T> dynamicNode(WorkflowElementIdentifier id) {
        return new DynamicNodeFactory<>((T) this, (NodeContainer) node, id);
    }

    public WorkItemNodeFactory<T> workItemNode(WorkflowElementIdentifier id) {
        return new WorkItemNodeFactory<>((T) this, (NodeContainer) node, id);
    }

    public EventSubProcessNodeFactory<T> eventSubProcessNode(WorkflowElementIdentifier id) {
        return new EventSubProcessNodeFactory<>((T) this, (NodeContainer) node, id);
    }

    public StateNodeFactory<T> stateNode(WorkflowElementIdentifier id) {
        return new StateNodeFactory<>((T) this, (NodeContainer) node, id);
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
        Node from = ((NodeContainer) node).getNode(fromId);
        Node to = ((NodeContainer) node).getNode(toId);
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
        ContextContainer contextContainer = (ContextContainer) node;
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
        if (node instanceof ContextContainer) {
            CompensationScope compensationScope = new CompensationScope();
            ContextContainer contextNode = (ContextContainer) node;
            contextNode.addContext(compensationScope);
            contextNode.setDefaultContext(compensationScope);
            compensationScope.setContextContainerId(contextId);
        }
        return this;
    }
}
