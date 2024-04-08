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
package org.jbpm.workflow.instance.node;

import java.util.Collection;

import org.drools.core.common.InternalAgenda;
import org.drools.core.rule.consequence.InternalMatch;
import org.jbpm.workflow.core.Constraint;
import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.core.impl.ExtendedNodeImpl;
import org.jbpm.workflow.core.node.StateNode;
import org.jbpm.workflow.instance.NodeInstanceContainer;
import org.kie.api.definition.process.Connection;
import org.kie.api.event.rule.MatchCreatedEvent;
import org.kie.api.runtime.process.EventListener;
import org.kie.kogito.internal.process.runtime.KogitoNodeInstance;

public class StateNodeInstance extends CompositeContextNodeInstance implements EventListener {

    private static final long serialVersionUID = 510l;

    protected StateNode getStateNode() {
        return (StateNode) getNode();
    }

    @Override
    public void internalTrigger(KogitoNodeInstance from, String type) {
        super.internalTrigger(from, type);
        // if node instance was cancelled, abort
        if (getNodeInstanceContainer().getNodeInstance(getStringId()) == null) {
            return;
        }
        // TODO: composite states trigger
        StateNode stateNode = getStateNode();
        Connection selected = null;
        int priority = Integer.MAX_VALUE;
        for (Connection connection : stateNode.getOutgoingConnections(Node.CONNECTION_DEFAULT_TYPE)) {

            Collection<Constraint> constraints = stateNode.getConstraints(connection);

            if (constraints != null) {
                for (Constraint constraint : constraints) {
                    if (constraint.getPriority() < priority) {
                        String rule = "RuleFlowStateNode-" + getProcessInstance().getProcessId() + "-" +
                                getStateNode().getUniqueId() + "-" +
                                connection.getTo().getId() + "-" +
                                connection.getToType();
                        boolean isActive = ((InternalAgenda) getProcessInstance().getKnowledgeRuntime().getAgenda())
                                .isRuleActiveInRuleFlowGroup("DROOLS_SYSTEM", rule, getProcessInstance().getStringId());
                        if (isActive) {
                            selected = connection;
                            priority = constraint.getPriority();
                        }
                    }
                }
            }
        }
        if (selected != null) {
            ((NodeInstanceContainer) getNodeInstanceContainer()).removeNodeInstance(this);
            triggerConnection(selected);
        } else {
            addTriggerListener();
            addActivationListener();
        }

    }

    @Override
    protected boolean isLinkedIncomingNodeRequired() {
        return false;
    }

    @Override
    public void signalEvent(String type, Object event) {
        if ("signal".equals(type)) {
            if (event instanceof String) {
                for (Connection connection : getStateNode().getOutgoingConnections(Node.CONNECTION_DEFAULT_TYPE)) {
                    boolean selected = false;
                    Collection<Constraint> constraints = getStateNode().getConstraints(connection);
                    if (constraints == null) {
                        if (event.equals(connection.getTo().getName())) {
                            selected = true;
                        }
                    } else
                        for (Constraint constraint : constraints) {

                            if (event.equals(constraint.getName())) {
                                selected = true;
                                break;
                            }
                        }
                    if (selected) {
                        triggerEvent(ExtendedNodeImpl.EVENT_NODE_EXIT);
                        removeEventListeners();
                        ((org.jbpm.workflow.instance.NodeInstanceContainer) getNodeInstanceContainer())
                                .removeNodeInstance(this);
                        triggerConnection(connection);
                        return;
                    }
                }
            }
        } else if (getActivationEventType().equals(type)) {
            if (event instanceof MatchCreatedEvent) {
                activationCreated((MatchCreatedEvent) event);
            }
        } else {
            super.signalEvent(type, event);
        }
    }

    private void addTriggerListener() {
        getProcessInstance().addEventListener("signal", this, false);
    }

    private void addActivationListener() {
        getProcessInstance().addEventListener(getActivationEventType(), this, true);
    }

    @Override
    public void addEventListeners() {
        super.addEventListeners();
        addTriggerListener();
        addActivationListener();
    }

    @Override
    public void removeEventListeners() {
        super.removeEventListeners();
        getProcessInstance().removeEventListener("signal", this, false);
        getProcessInstance().removeEventListener(getActivationEventType(), this, true);
    }

    @Override
    public String[] getEventTypes() {
        return new String[] { "signal", getActivationEventType() };
    }

    private String getActivationEventType() {
        return "RuleFlowStateNode-" + getProcessInstance().getProcessId()
                + "-" + getStateNode().getUniqueId();
    }

    public void activationCreated(MatchCreatedEvent event) {
        Connection selected = null;
        for (Connection connection : getNode().getOutgoingConnections(Node.CONNECTION_DEFAULT_TYPE)) {
            Collection<Constraint> constraints = getStateNode().getConstraints(connection);
            if (constraints != null) {
                String constraintName = getActivationEventType() + "-"
                        + connection.getTo().getId().toExternalFormat() + "-" + connection.getToType();
                if (constraintName.equals(event.getMatch().getRule().getName())
                        && checkProcessInstance((InternalMatch) event.getMatch())) {
                    selected = connection;
                }

            }
        }
        if (selected != null) {
            removeEventListeners();
            ((NodeInstanceContainer) getNodeInstanceContainer()).removeNodeInstance(this);
            triggerConnection(selected);
        }
    }

}
