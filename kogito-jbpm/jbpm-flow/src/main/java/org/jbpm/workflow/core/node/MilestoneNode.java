/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.workflow.core.node;

import org.kie.api.definition.process.Connection;
import org.jbpm.workflow.core.Constraint;
import org.jbpm.workflow.core.impl.ConnectionRef;

/**
 * Default implementation of a milestone node.
 * 
 */
public class MilestoneNode extends StateBasedNode implements Constrainable {

	private static final long serialVersionUID = 510l;

	private String constraint;
	private String matchVariable;

    public void addConstraint(ConnectionRef connection, Constraint constraint) {
    	if (connection != null) {
    		throw new IllegalArgumentException(
				"A Milestone node only accepts one simple constraint");
    	}
        this.constraint = constraint.getConstraint();
    }
    
    public void setConstraint(String constraint){
        this.constraint = constraint;
    }

    public String getConstraint(){
        return this.constraint;
    }    
    
    public String getMatchVariable() {
        return matchVariable;
    }
    
    public void setMatchVariable(String matchVariable) {
        this.matchVariable = matchVariable;
    }

    public void validateAddIncomingConnection(final String type, final Connection connection) {
        super.validateAddIncomingConnection(type, connection);
        if (!org.jbpm.workflow.core.Node.CONNECTION_DEFAULT_TYPE.equals(type)) {
        	throw new IllegalArgumentException(
                    "This type of node [" + connection.getTo().getMetaData().get("UniqueId") + ", " + connection.getTo().getName() 
                    + "] only accepts default incoming connection type!");
        }
        if (getFrom() != null && !"true".equals(System.getProperty("jbpm.enable.multi.con"))) {
        	throw new IllegalArgumentException(
                    "This type of node [" + connection.getTo().getMetaData().get("UniqueId") + ", " + connection.getTo().getName() 
                    + "] cannot have more than one incoming connection!");
        }
    }

    public void validateAddOutgoingConnection(final String type, final Connection connection) {
        super.validateAddOutgoingConnection(type, connection);
        if (!org.jbpm.workflow.core.Node.CONNECTION_DEFAULT_TYPE.equals(type)) {
        	throw new IllegalArgumentException(
                    "This type of node [" + connection.getFrom().getMetaData().get("UniqueId") + ", " + connection.getFrom().getName() 
                    + "] only accepts default outgoing connection type!");
        }
        if (getTo() != null && !"true".equals(System.getProperty("jbpm.enable.multi.con"))) {
        	throw new IllegalArgumentException(
                    "This type of node [" + connection.getFrom().getMetaData().get("UniqueId") + ", " + connection.getFrom().getName() 
                    + "] cannot have more than one outgoing connection!");
        }
    }
    
}
