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
package org.jbpm.workflow.core.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.jbpm.process.core.Context;
import org.jbpm.process.core.ContextResolver;
import org.jbpm.process.core.context.variable.Mappable;
import org.jbpm.process.instance.impl.ReturnValueConstraintEvaluator;
import org.jbpm.ruleflow.core.Metadata;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.jbpm.ruleflow.core.WorkflowElementIdentifierFactory;
import org.jbpm.workflow.core.Constraint;
import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.core.WorkflowProcess;
import org.kie.api.definition.process.Connection;
import org.kie.api.definition.process.NodeContainer;
import org.kie.api.definition.process.WorkflowElementIdentifier;

import static org.jbpm.workflow.instance.WorkflowProcessParameters.WORKFLOW_PARAM_MULTIPLE_CONNECTIONS;

/**
 * Default implementation of a node.
 */
public abstract class NodeImpl implements Node, ContextResolver, Mappable {

    private static final long serialVersionUID = 510l;

    private WorkflowElementIdentifier id;

    private String name;
    private Map<String, List<Connection>> incomingConnections;
    private Map<String, List<Connection>> outgoingConnections;
    private NodeContainer parentContainer;
    private Map<String, Context> contexts = new HashMap<>();
    private Map<String, Object> metaData = new HashMap<>();

    protected Map<ConnectionRef, Collection<Constraint>> constraints = new HashMap<>();

    private IOSpecification ioSpecification;
    private MultiInstanceSpecification multiInstanceSpecification;

    public NodeImpl() {
        this.id = WorkflowElementIdentifierFactory.newRandom();
        this.incomingConnections = new HashMap<>();
        this.outgoingConnections = new HashMap<>();
        this.ioSpecification = new IOSpecification();
        this.multiInstanceSpecification = new MultiInstanceSpecification();
    }

    public void setMultiInstanceSpecification(MultiInstanceSpecification multiInstanceSpecification) {
        this.multiInstanceSpecification = multiInstanceSpecification;
    }

    public MultiInstanceSpecification getMultiInstanceSpecification() {
        return multiInstanceSpecification;
    }

    public void setIoSpecification(IOSpecification ioSpecification) {
        this.ioSpecification = ioSpecification;
    }

    public IOSpecification getIoSpecification() {
        return ioSpecification;
    }

    @Override
    public Map<String, String> getInMappings() {
        return getIoSpecification().getInputMapping();
    }

    @Override
    public Map<String, String> getOutMappings() {
        return getIoSpecification().getOutputMappingBySources();
    }

    @Override
    public String getInMapping(String key) {
        return getIoSpecification().getInputMapping().get(key);
    }

    @Override
    public String getOutMapping(String key) {
        return getIoSpecification().getOutputMappingBySources().get(key);
    }

    @Override
    public void addInMapping(String from, String to) {
        getIoSpecification().addInputMapping(from, to);
    }

    @Override
    public void addOutMapping(String from, String to) {
        getIoSpecification().addOutputMapping(from, to);
    }

    @Override
    public void addInAssociation(DataAssociation dataAssociation) {
        getIoSpecification().getDataInputs().add(dataAssociation.getTarget());
        getIoSpecification().getDataInputAssociation().add(dataAssociation);
    }

    @Override
    public List<DataAssociation> getInAssociations() {
        return getIoSpecification().getDataInputAssociation();
    }

    @Override
    public void addOutAssociation(DataAssociation dataAssociation) {
        dataAssociation.getSources().forEach(s -> getIoSpecification().getDataOutputs().add(s));
        getIoSpecification().getDataOutputAssociation().add(dataAssociation);
    }

    public WorkflowProcess getProcess() {
        NodeContainer container = parentContainer;
        while (!(container instanceof RuleFlowProcess)) {
            container = ((NodeImpl) container).parentContainer;
        }
        return (WorkflowProcess) container;
    }

    @Override
    public List<DataAssociation> getOutAssociations() {
        return getIoSpecification().getDataOutputAssociation();
    }

    @Override
    public WorkflowElementIdentifier getId() {
        return this.id;
    }

    @Override
    public void setId(WorkflowElementIdentifier id) {
        this.id = id;
        String uniqueId = (String) getMetaData(Metadata.UNIQUE_ID);
        if (uniqueId == null) {
            setMetaData(Metadata.UNIQUE_ID, id.toExternalFormat());
        }
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(final String name) {
        this.name = name;
    }

    @Override
    public Map<String, List<Connection>> getIncomingConnections() {
        // TODO: users can still modify the lists inside this Map
        return Collections.unmodifiableMap(this.incomingConnections);
    }

    @Override
    public Map<String, List<Connection>> getOutgoingConnections() {
        // TODO: users can still modify the lists inside this Map
        return Collections.unmodifiableMap(this.outgoingConnections);
    }

    @Override
    public void addIncomingConnection(final String type, final Connection connection) {
        validateAddIncomingConnection(type, connection);
        List<Connection> connections = this.incomingConnections.get(type);
        if (connections == null) {
            connections = new ArrayList<>();
            this.incomingConnections.put(type, connections);
        }
        connections.add(connection);
    }

    public void validateAddIncomingConnection(final String type, final Connection connection) {
        if (type == null) {
            throw new IllegalArgumentException("Connection type cannot be null");
        }
        if (connection == null) {
            throw new IllegalArgumentException("Connection cannot be null");
        }
    }

    @Override
    public List<Connection> getIncomingConnections(String type) {
        List<Connection> result = incomingConnections.get(type);
        if (result == null) {
            return new ArrayList<>();
        }
        return result;
    }

    @Override
    public void addOutgoingConnection(final String type, final Connection connection) {
        validateAddOutgoingConnection(type, connection);
        List<Connection> connections = this.outgoingConnections.get(type);
        if (connections == null) {
            connections = new ArrayList<>();
            this.outgoingConnections.put(type, connections);
        }
        connections.add(connection);
    }

    public void validateAddOutgoingConnection(final String type, final Connection connection) {
        if (type == null) {
            throw new IllegalArgumentException("Connection type cannot be null");
        }
        if (connection == null) {
            throw new IllegalArgumentException("Connection cannot be null");
        }
    }

    @Override
    public List<Connection> getOutgoingConnections(String type) {
        List<Connection> result = outgoingConnections.get(type);
        if (result == null) {
            return new ArrayList<>();
        }
        return result;
    }

    @Override
    public void removeIncomingConnection(final String type, final Connection connection) {
        validateRemoveIncomingConnection(type, connection);
        this.incomingConnections.get(type).remove(connection);
    }

    public void clearIncomingConnection() {
        this.incomingConnections.clear();
    }

    public void clearOutgoingConnection() {
        this.outgoingConnections.clear();
    }

    public void validateRemoveIncomingConnection(final String type, final Connection connection) {
        if (type == null) {
            throw new IllegalArgumentException("Connection type cannot be null");
        }
        if (connection == null) {
            throw new IllegalArgumentException("Connection is null");
        }
        if (!incomingConnections.get(type).contains(connection)) {
            throw new IllegalArgumentException("Given connection <"
                    + connection + "> is not part of the incoming connections");
        }
    }

    @Override
    public void removeOutgoingConnection(final String type, final Connection connection) {
        validateRemoveOutgoingConnection(type, connection);
        this.outgoingConnections.get(type).remove(connection);
    }

    public void validateRemoveOutgoingConnection(final String type, final Connection connection) {
        if (type == null) {
            throw new IllegalArgumentException("Connection type cannot be null");
        }
        if (connection == null) {
            throw new IllegalArgumentException("Connection is null");
        }
        if (!this.outgoingConnections.get(type).contains(connection)) {
            throw new IllegalArgumentException("Given connection <"
                    + connection + "> is not part of the outgoing connections");
        }
    }

    /**
     * Helper method for nodes that have at most one default incoming connection
     */
    public Connection getFrom() {
        final List<Connection> list =
                getIncomingConnections(Node.CONNECTION_DEFAULT_TYPE);
        if (list.isEmpty()) {
            return null;
        }
        if (list.size() == 1) {
            return list.get(0);
        }
        if (WORKFLOW_PARAM_MULTIPLE_CONNECTIONS.get(getProcess())) {
            return list.get(0);
        } else {
            throw new IllegalArgumentException(
                    "Trying to retrieve the from connection but multiple connections are present");
        }
    }

    /**
     * Helper method for nodes that have at most one default outgoing connection
     */
    public Connection getTo() {
        final List<Connection> list =
                getOutgoingConnections(Node.CONNECTION_DEFAULT_TYPE);
        if (list.isEmpty()) {
            return null;
        }
        if (list.size() == 1) {
            return list.get(0);
        }
        if (WORKFLOW_PARAM_MULTIPLE_CONNECTIONS.get(getProcess())) {
            return list.get(0);
        } else {
            throw new IllegalArgumentException(
                    "Trying to retrieve the to connection but multiple connections are present");
        }
    }

    /**
     * Helper method for nodes that have multiple default incoming connections
     */
    public List<Connection> getDefaultIncomingConnections() {
        return getIncomingConnections(Node.CONNECTION_DEFAULT_TYPE);
    }

    /**
     * Helper method for nodes that have multiple default outgoing connections
     */
    public List<Connection> getDefaultOutgoingConnections() {
        return getOutgoingConnections(Node.CONNECTION_DEFAULT_TYPE);
    }

    @Override
    public NodeContainer getParentContainer() {
        return parentContainer;
    }

    @Override
    public void setParentContainer(NodeContainer nodeContainer) {
        this.parentContainer = nodeContainer;
    }

    @Override
    public void setContext(String contextId, Context context) {
        this.contexts.put(contextId, context);
    }

    @Override
    public Context getContext(String contextId) {
        return this.contexts.get(contextId);
    }

    @Override
    public Context resolveContext(String contextId, Object param) {
        Context context = getContext(contextId);
        if (context != null) {
            context = context.resolveContext(param);
            if (context != null) {
                return context;
            }
        }
        return ((org.jbpm.workflow.core.NodeContainer) parentContainer).resolveContext(contextId, param);
    }

    @Override
    public void setMetaData(String name, Object value) {
        this.metaData.put(name, value);
    }

    public Object getMetaData(String name) {
        return this.metaData.get(name);
    }

    @Override
    public Map<String, Object> getMetaData() {
        return this.metaData;
    }

    public void setMetaData(Map<String, Object> metaData) {
        this.metaData = metaData;
    }

    public Collection<Constraint> getConstraints(final Connection connection) {
        if (connection == null) {
            throw new IllegalArgumentException("connection is null");
        }

        ConnectionRef ref = new ConnectionRef(connection.getUniqueId(), connection.getTo().getId(), connection.getToType());
        return this.constraints.get(ref);
    }

    public Constraint getConstraint(final Connection connection) {
        Collection<Constraint> constraints = getConstraints(connection);
        return constraints != null ? constraints.iterator().next() : null;
    }

    public Constraint internalGetConstraint(final ConnectionRef ref) {
        Collection<Constraint> constraints = this.constraints.get(ref);
        return constraints != null ? constraints.iterator().next() : null;
    }

    public void setConstraint(final Connection connection,
            final Constraint constraint) {
        if (connection == null) {
            throw new IllegalArgumentException("connection is null");
        }
        if (!getDefaultOutgoingConnections().contains(connection)) {
            throw new IllegalArgumentException("connection is unknown:" + connection);
        }
        addConstraint(
                new ConnectionRef(connection.getUniqueId(), connection.getTo().getId(), connection.getToType()),
                constraint);

    }

    public void addConstraint(ConnectionRef connectionRef, Constraint constraint) {
        if (connectionRef == null) {
            throw new IllegalArgumentException(
                    "A " + this.getName() + " node only accepts constraints linked to a connection");
        }
        Collection<Constraint> values = this.constraints.computeIfAbsent(connectionRef, r -> new ArrayList<>());
        values.removeIf(v -> !ReturnValueConstraintEvaluator.class.isInstance(v) && Objects.equals(v.getConstraint(), constraint.getConstraint()));
        values.add(constraint);
    }

    public Map<ConnectionRef, Collection<Constraint>> getConstraints() {
        return Collections.unmodifiableMap(this.constraints);
    }

    @Override
    public NodeContainer getNodeContainer() {
        return getParentContainer();
    }
}
