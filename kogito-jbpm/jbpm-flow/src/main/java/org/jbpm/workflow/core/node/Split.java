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

import java.util.Collection;

import org.jbpm.workflow.core.Constraint;
import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.core.impl.ConnectionRef;
import org.jbpm.workflow.core.impl.NodeImpl;
import org.kie.api.definition.process.Connection;
import org.kie.api.definition.process.NodeType;

import static org.jbpm.workflow.instance.WorkflowProcessParameters.WORKFLOW_PARAM_MULTIPLE_CONNECTIONS;

/**
 * Default implementation of a split node.
 * 
 */
public class Split extends NodeImpl implements Constrainable {

    public static final int TYPE_UNDEFINED = 0;
    /**
     * All outgoing connections of a split of this type are triggered
     * when its incoming connection has been triggered. A split of this
     * type should have no constraints linked to any of its outgoing
     * connections.
     */
    public static final int TYPE_AND = 1;
    /**
     * Exactly one outgoing connection of a split of this type is triggered
     * when its incoming connection has been triggered. Which connection
     * is based on the constraints associated with each of the connections:
     * the connection with the highest priority whose constraint is satisfied
     * is triggered.
     */
    public static final int TYPE_XOR = 2;
    /**
     * One or multiple outgoing connections of a split of this type are
     * triggered when its incoming connection has been triggered. Which
     * connections is based on the constraints associated with each of the
     * connections: all connections whose constraint is satisfied are
     * triggered.
     */
    public static final int TYPE_OR = 3;
    public static final int TYPE_XAND = 4;

    private static final long serialVersionUID = 510l;

    private int type;

    public Split() {
        super(NodeType.COMPLEX_GATEWAY);
        this.type = TYPE_UNDEFINED;
    }

    public Split(final int type) {
        super(fromType(type));
        this.type = type;
    }

    public void setType(final int type) {
        this.type = type;
        setNodeType(fromType(type));
    }

    private static NodeType fromType(int type) {
        return switch (type) {
            case TYPE_AND -> NodeType.PARALLEL_GATEWAY;
            case TYPE_XAND -> NodeType.EVENT_BASED_GATEWAY;
            case TYPE_OR -> NodeType.INCLUSIVE_GATEWAY;
            case TYPE_XOR -> NodeType.EXCLUSIVE_GATEWAY;
            default -> NodeType.COMPLEX_GATEWAY;
        };
    }

    public int getType() {
        return this.type;
    }

    public boolean isDefault(final Connection connection) {
        if (connection == null) {
            throw new IllegalArgumentException("connection is null");
        }

        if (this.type == TYPE_OR || this.type == TYPE_XOR) {
            ConnectionRef ref = new ConnectionRef(connection.getUniqueId(), connection.getTo().getId(), connection.getToType());
            Collection<Constraint> constraints = this.constraints.get(ref);
            if (constraints != null) {
                for (Constraint constraint : constraints) {
                    if (constraint != null) {
                        return constraint.isDefault();
                    }
                }
            }
            String defaultConnection = (String) getMetaData().get("Default");
            String connectionId = (String) connection.getMetaData().get("UniqueId");
            return connectionId.equals(defaultConnection);
        }
        throw new UnsupportedOperationException("Constraints are " +
                "only supported with XOR or OR split types, not with: " + getType());
    }

    @Override
    public Collection<Constraint> getConstraints(final Connection connection) {
        if (connection == null) {
            throw new IllegalArgumentException("connection is null");
        }

        if (this.type == TYPE_OR || this.type == TYPE_XOR) {
            ConnectionRef ref = new ConnectionRef(connection.getUniqueId(), connection.getTo().getId(), connection.getToType());
            return this.constraints.get(ref);
        }
        throw new UnsupportedOperationException("Constraints are " +
                "only supported with XOR or OR split types, not with: " + getType());
    }

    @Override
    public void setConstraint(final Connection connection,
            final Constraint constraint) {
        if (this.type == TYPE_OR || this.type == TYPE_XOR) {
            if (connection == null) {
                throw new IllegalArgumentException("connection is null");
            }
            if (!getDefaultOutgoingConnections().contains(connection)) {
                throw new IllegalArgumentException("connection is unknown:" + connection);
            }
            addConstraint(
                    new ConnectionRef(connection.getUniqueId(), connection.getTo().getId(), connection.getToType()),
                    constraint);
        } else {
            throw new UnsupportedOperationException("Constraints are " +
                    "only supported with XOR or OR split types, not with type:" + getType());
        }
    }

    @Override
    public void validateAddIncomingConnection(final String type, final Connection connection) {
        super.validateAddIncomingConnection(type, connection);
        if (!Node.CONNECTION_DEFAULT_TYPE.equals(type)) {
            throw new IllegalArgumentException(
                    "This type of node [" + connection.getTo().getUniqueId() + ", " + connection.getTo().getName()
                            + "] only accepts default incoming connection type!");
        }

        if (!getIncomingConnections(Node.CONNECTION_DEFAULT_TYPE).isEmpty() && !WORKFLOW_PARAM_MULTIPLE_CONNECTIONS.get(getProcess())) {
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
    }

    @Override
    public void removeOutgoingConnection(final String type, final Connection connection) {
        super.removeOutgoingConnection(type, connection);
        removeConstraint(connection);
    }

    public void removeConstraint(Connection connection) {
        ConnectionRef ref = new ConnectionRef(connection.getUniqueId(), connection.getTo().getId(), connection.getToType());
        internalRemoveConstraint(ref);
    }

    public void internalRemoveConstraint(ConnectionRef ref) {
        this.constraints.remove(ref);
    }

}
