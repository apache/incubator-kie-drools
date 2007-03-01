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

import java.util.List;

import org.drools.ruleflow.core.IConnection;
import org.drools.ruleflow.core.IStartNode;

/**
 * Default implementation of a start node.
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public class StartNode extends Node implements IStartNode {

    private static final long serialVersionUID = 3257564005806782517L;
    
    public IConnection getTo() {
    	List list = getOutgoingConnections();
    	if (list.size() > 0) {
    		return (IConnection) list.get(0);
    	}
		return null;
    }
    
    protected void validateAddOutgoingConnection(IConnection connection) {
    	super.validateAddOutgoingConnection(connection);
    	if (getOutgoingConnections().size() > 0) {    		
            throw new IllegalArgumentException(
        		"A start node cannot have more than one outgoing connection");
        }
    }


    protected void validateAddIncomingConnection(IConnection connection) {
        throw new UnsupportedOperationException(
    		"A start node does not have an incoming connection");
    }

    protected void validateRemoveIncomingConnection(IConnection connection) {
        throw new UnsupportedOperationException(
    		"A start node does not have an incoming connection");
    }

}
