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
package org.jbpm.workflow.core.node;

import java.util.Collections;
import java.util.List;

import org.jbpm.workflow.core.DroolsAction;
import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.core.impl.DataAssociation;
import org.jbpm.workflow.core.impl.ExtendedNodeImpl;
import org.kie.api.definition.process.Connection;
import org.kie.api.definition.process.NodeType;

import static org.jbpm.workflow.instance.WorkflowProcessParameters.WORKFLOW_PARAM_MULTIPLE_CONNECTIONS;

/**
 * Default implementation of an action node.
 * 
 */
public class ActionNode extends ExtendedNodeImpl {

    private static final long serialVersionUID = 510l;

    private DroolsAction action;

    public ActionNode() {
        super(NodeType.SCRIPT_TASK);
    }

    public ActionNode(NodeType nodeType) {
        super(nodeType);
    }

    public DroolsAction getAction() {
        return action;
    }

    public void setAction(DroolsAction action) {
        this.action = action;
    }

    @Override
    public void validateAddIncomingConnection(final String type, final Connection connection) {
        super.validateAddIncomingConnection(type, connection);
        if (!Node.CONNECTION_DEFAULT_TYPE.equals(type)) {
            throw new IllegalArgumentException(
                    "This type of node [" + connection.getTo().getUniqueId() + ", " + connection.getTo().getName()
                            + "] only accepts default incoming connection type!");
        }
        if (getFrom() != null && !WORKFLOW_PARAM_MULTIPLE_CONNECTIONS.get(getProcess())) {
            throw new IllegalArgumentException(
                    "This type of node [" + connection.getTo().getUniqueId() + ", " + connection.getTo().getName()
                            + "] cannot have more than one incoming connection!");
        }
    }

    @Override
    public void validateAddOutgoingConnection(final String type, final Connection connection) {
        super.validateAddOutgoingConnection(type, connection);
        if (!Node.CONNECTION_DEFAULT_TYPE.equals(type)) {
            throw new IllegalArgumentException(
                    "This type of node [" + connection.getFrom().getUniqueId() + ", " + connection.getFrom().getName()
                            + "] only accepts default outgoing connection type!");
        }
        if (getTo() != null && !WORKFLOW_PARAM_MULTIPLE_CONNECTIONS.get(getProcess())) {
            throw new IllegalArgumentException(
                    "This type of node [" + connection.getFrom().getUniqueId() + ", " + connection.getFrom().getName()
                            + "] cannot have more than one outgoing connection!");
        }
    }

    @Override
    public List<DataAssociation> getOutAssociations() {
        return Collections.unmodifiableList(getIoSpecification().getDataOutputAssociation());
    }

}
