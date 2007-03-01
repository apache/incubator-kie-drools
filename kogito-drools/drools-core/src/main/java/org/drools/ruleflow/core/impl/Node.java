package org.drools.ruleflow.core.impl;
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.drools.ruleflow.core.IConnection;
import org.drools.ruleflow.core.INode;

/**
 * Default implementation of a node.
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public abstract class Node implements INode, Serializable {
    
    protected static final Node[] EMPTY_NODE_ARRAY = new Node[0];
    
    private long id;
    private String name;
    private List incomingConnections;
    private List outgoingConnections;
    
    public Node() {
        this.id = -1;
        incomingConnections = new ArrayList();
        outgoingConnections = new ArrayList();        
    }
    
    public long getId() {
        return id;
    }
    
    public void setId(long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
        
    public List getIncomingConnections() {
    	return Collections.unmodifiableList(incomingConnections);
    }
    
    public List getOutgoingConnections() {
    	return Collections.unmodifiableList(outgoingConnections);
    }
    
    protected void addIncomingConnection(IConnection connection) {
    	validateAddIncomingConnection(connection);
    	incomingConnections.add(connection);
    }
    
    /**
	 * This method validates whether the given connection can be added. If the
	 * connection cannot be added, an IllegalArgumentException is thrown.
	 * <p>
	 * 
	 * @param connection
	 *            the incoming connection to be added
	 * @throws IllegalArgumentException
	 *             is thrown if the connection is null, or if a connection is
	 *             added twice. If subclasses want to change the rules for
	 *             adding incoming connections the
	 *             <code>validateAddIncomingConnection(IConnection connection)</code>
	 *             should be overridden.
	 */
    protected void validateAddIncomingConnection(IConnection connection) {
    	if (connection == null) {
    		throw new IllegalArgumentException("Connection cannot be null");
    	}
    	if (incomingConnections.contains(connection)) {
    		throw new IllegalArgumentException("Connection is already added");
    	}
    }
    
    protected void addOutgoingConnection(IConnection connection) {
    	validateAddOutgoingConnection(connection);
    	outgoingConnections.add(connection);
    }
    
    /**
	 * This method validates whether the given connection can be added. If the
	 * connection cannot be added, an IllegalArgumentException is thrown.
	 * <p>
	 * 
	 * @param connection
	 *            the outgoin connection to be added
	 * @throws IllegalArgumentException
	 *             is thrown if the connection is null, or if a connection is
	 *             added twice. If subclasses want to change the rules for
	 *             adding outgoing connections the
	 *             <code>validateAddIncomingConnection(IConnection connection)</code>
	 *             should be overridden.
	 */
    protected void validateAddOutgoingConnection(IConnection connection) {
    	if (connection == null) {
    		throw new IllegalArgumentException("Connection cannot be null");
    	}
    	if (outgoingConnections.contains(connection)) {
    		throw new IllegalArgumentException("Connection is already added");
    	}
    }
    
    protected void removeIncomingConnection(IConnection connection) {
    	validateRemoveIncomingConnection(connection);
    	incomingConnections.remove(connection);
    }
    
    /**
	 * This method validates whether the given connection can be removed
	 * <p>
	 * 
	 * @param connection
	 *            the incoming connection
	 * @throws IllegalArgumentException
	 *             is thrown if connectin is null, or unknown. If subclasses
	 *             want to change the rules for removing incoming connections
	 *             the
	 *             <code>validateRemoveIncomingConnection(IConnection connection)</code>
	 *             should be overridden.
	 */
    protected void validateRemoveIncomingConnection(IConnection connection) {
    	if (connection == null) {
    		throw new IllegalArgumentException("Connection is null");
    	}
    	if (!incomingConnections.contains(connection)) {
    		throw new IllegalArgumentException("Given connection <"
				+ connection + "> is not part of the incoming connections");
    	}
    }
    
    protected void removeOutgoingConnection(IConnection connection) {
    	validateRemoveOutgoingConnection(connection);
    	outgoingConnections.remove(connection);
    }
    
    /**
	 * This method validates whether the given connection can be removed
	 * <p>
	 * 
	 * @param connection
	 *            the outgoing connection
	 * @throws IllegalArgumentException
	 *             is thrown if connectin is null, or unknown. If subclasses
	 *             want to change the rules for removing outgoing connections
	 *             the
	 *             <code>validateRemoveOutgoingConnection(IConnection connection)</code>
	 *             should be overridden.
	 */
    protected void validateRemoveOutgoingConnection(IConnection connection) {
    	if (connection == null) {
    		throw new IllegalArgumentException("Connection is null");
    	}
    	if (!outgoingConnections.contains(connection)) {
    		throw new IllegalArgumentException("Given connection <"
				+ connection + "> is not part of the outgoing connections");
    	}
    }
}
