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

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.drools.ruleflow.core.IConnection;
import org.drools.ruleflow.core.IConstraint;
import org.drools.ruleflow.core.ISplit;

/**
 * Default implementation of a split node.
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public class Split extends Node implements ISplit {
    
    private static final long serialVersionUID = 3258413949669159736L;

    private int type;
    private Map constraints;
        
    public Split() {
    	type = TYPE_UNDEFINED;
    	constraints = new HashMap();    	
    }
    
    public Split(int type) {
    	this.type = type;
    	constraints = new HashMap();
    }
    
    public void setType(int type) {
        this.type = type;
    }
    
    public int getType() {
        return type;
    }
    
    public IConstraint getConstraint(IConnection connection) {
    	if (connection == null) {
    		throw new IllegalArgumentException("connection is null");
    	}
    	
    	// dirty hack because keys were entered wrong
    	// probably caused by xstreams
    	HashMap newMap = new HashMap();
		for (Iterator it = constraints.entrySet().iterator(); it.hasNext(); ) {
			Entry entry = (Entry) it.next();
			newMap.put(entry.getKey(), entry.getValue());
		}
		constraints = newMap;
		
    	if (type == TYPE_OR || type == TYPE_XOR) {
    		return (IConstraint) constraints.get(connection);
    	}
		throw new UnsupportedOperationException("Constraints are "
			+ "only supported with XOR or OR split types, not with: "
			+ getType());
    }
    
    public void setConstraint(IConnection connection, IConstraint constraint) {    	  	
    	if (type == TYPE_OR || type == TYPE_XOR) {
    		if (connection == null) {
        		throw new IllegalArgumentException("connection is null");
        	}  
    		if (!getOutgoingConnections().contains(connection)) {
        		throw new IllegalArgumentException("connection is unknown:" + connection);
        	}
    		constraints.put(connection, constraint);
    	} else {
    		throw new UnsupportedOperationException("Constraints are "
    				+ "only supported with XOR or OR split types, not with type:"
    				+ getType());
    	} 
    }
    
    public Map getConstraints() {
    	if (type == TYPE_OR || type == TYPE_XOR) {
    		return Collections.unmodifiableMap(constraints);
    	}
		throw new UnsupportedOperationException("Constraints are "
			+ "only supported with XOR or OR split types, not with: "
			+ getType());
    }
    
    public IConnection getFrom() {
    	if (getIncomingConnections().size() > 0) {
    		return (IConnection) getIncomingConnections().get(0);
    	}
		return null;
    }
    
    protected void validateAddIncomingConnection(IConnection connection) {
    	super.validateAddIncomingConnection(connection);
    	if (getIncomingConnections().size() > 0) {
            throw new IllegalArgumentException("A split cannot have more than one incoming connection");
        }
    }
   
    protected void validateAddOutgoingConnection(IConnection connection) {
    	super.validateAddOutgoingConnection(connection);
    	if (connection.getType() != IConnection.TYPE_NORMAL) {
            throw new IllegalArgumentException("Unknown connection type :" + connection.getType()
            		+ ", only NORMAL is allowed as outgoing connection.");
        }    	
    }
    
    public void removeOutgoingConnection(IConnection connection) {
        super.removeOutgoingConnection(connection);
        constraints.remove(connection);
    }
}
