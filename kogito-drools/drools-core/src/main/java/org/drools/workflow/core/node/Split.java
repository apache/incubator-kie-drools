package org.drools.workflow.core.node;

/*
 * Copyright 2005 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.drools.definition.process.Connection;
import org.drools.workflow.core.Constraint;
import org.drools.workflow.core.impl.ConnectionRef;
import org.drools.workflow.core.impl.NodeImpl;

/**
 * Default implementation of a split node.
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public class Split extends NodeImpl implements Constrainable {

    public static final int TYPE_UNDEFINED = 0;
    /**
     * All outgoing connections of a split of this type are triggered
     * when its incoming connection has been triggered.  A split of this
     * type should have no constraints linked to any of its outgoing
     * connections.
     */
    public static final int TYPE_AND       = 1;
    /**
     * Exactly one outgoing connection of a split of this type is triggered
     * when its incoming connection has been triggered.  Which connection
     * is based on the constraints associated with each of the connections:
     * the connection with the highest priority whose constraint is satisfied
     * is triggered.  
     */
    public static final int TYPE_XOR       = 2;
    /**
     * One or multiple outgoing connections of a split of this type are
     * triggered when its incoming connection has been triggered.  Which
     * connections is based on the constraints associated with each of the
     * connections: all connections whose constraint is satisfied are
     * triggered.  
     */
    public static final int TYPE_OR        = 3;

    private static final long serialVersionUID = 400L;

    private int type;
    private Map<ConnectionRef, Constraint> constraints = new HashMap<ConnectionRef, Constraint>();

    public Split() {
        this.type = TYPE_UNDEFINED;
    }

    public Split(final int type) {
        this.type = type;
    }    

    public void setType(final int type) {
        this.type = type;
    }

    public int getType() {
        return this.type;
    }

    public Constraint getConstraint(final Connection connection) {
        if ( connection == null ) {
            throw new IllegalArgumentException( "connection is null" );
        }

        if ( this.type == TYPE_OR || this.type == TYPE_XOR ) {
            ConnectionRef ref = new ConnectionRef(connection.getTo().getId(), connection.getToType());
            return this.constraints.get(ref);
        }
        throw new UnsupportedOperationException( "Constraints are " + "only supported with XOR or OR split types, not with: " + getType() );
    }

    public Constraint internalGetConstraint(final ConnectionRef ref) {
        return this.constraints.get(ref);
    }

    public void setConstraint(final Connection connection,
                              final Constraint constraint) {
        if ( this.type == TYPE_OR || this.type == TYPE_XOR ) {
            if ( connection == null ) {
                throw new IllegalArgumentException( "connection is null" );
            }
            if (!getDefaultOutgoingConnections().contains(connection)) {
                throw new IllegalArgumentException("connection is unknown:" + connection);
            }
            addConstraint(
                new ConnectionRef(connection.getTo().getId(), connection.getToType()),
                constraint);
        } else {
            throw new UnsupportedOperationException( "Constraints are " + "only supported with XOR or OR split types, not with type:" + getType() );
        }
    }

    public void addConstraint(ConnectionRef connectionRef, Constraint constraint) {
    	if (connectionRef == null) {
    		throw new IllegalArgumentException(
				"A split node only accepts constraints linked to a connection");
    	}
        this.constraints.put(connectionRef, constraint);
    }

    public Map<ConnectionRef, Constraint> getConstraints() {
        return Collections.unmodifiableMap( this.constraints );
    }

    public void validateAddIncomingConnection(final String type, final Connection connection) {
        super.validateAddIncomingConnection(type, connection);
        if (!org.drools.workflow.core.Node.CONNECTION_DEFAULT_TYPE.equals(type)) {
            throw new IllegalArgumentException(
                "This type of node only accepts default incoming connection type!");
        }
        if (!getIncomingConnections(org.drools.workflow.core.Node.CONNECTION_DEFAULT_TYPE).isEmpty()) {
            throw new IllegalArgumentException(
                "This type of node cannot have more than one incoming connection!");
        }
    }

    public void validateAddOutgoingConnection(final String type, final Connection connection) {
        super.validateAddOutgoingConnection(type, connection);
        if (!org.drools.workflow.core.Node.CONNECTION_DEFAULT_TYPE.equals(type)) {
            throw new IllegalArgumentException(
                "This type of node only accepts default outgoing connection type!");
        }
    }

    public void removeOutgoingConnection(final String type, final Connection connection) {
        super.removeOutgoingConnection(type, connection);
        removeConstraint(connection);
    }
    
    public void removeConstraint(Connection connection) {
    	ConnectionRef ref = new ConnectionRef(connection.getTo().getId(), connection.getToType());
        internalRemoveConstraint(ref);
    }
    
    public void internalRemoveConstraint(ConnectionRef ref) {
    	this.constraints.remove(ref);
    }
    
}
