/**
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

package org.jbpm.workflow.core.node;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.kie.definition.process.Connection;
import org.drools.process.core.Work;
import org.jbpm.process.core.Context;
import org.jbpm.process.core.ContextContainer;
import org.jbpm.process.core.context.AbstractContext;
import org.jbpm.process.core.context.variable.Mappable;

/**
 * Default implementation of a task node.
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public class WorkItemNode extends StateBasedNode implements Mappable, ContextContainer {

	private static final long serialVersionUID = 510l;
	
	private Work work;

	private List<DataAssociation> inMapping = new LinkedList<DataAssociation>();
	private List<DataAssociation> outMapping = new LinkedList<DataAssociation>();
    private boolean waitForCompletion = true;
    // TODO boolean independent (cancel work item if node gets cancelled?)

	public Work getWork() {
		return work;
	}

	public void setWork(Work work) {
		this.work = work;
	}
	

    public void addInMapping(String parameterName, String variableName) {
    	inMapping.add(new DataAssociation(variableName, parameterName, null, null));
    }

    public void setInMappings(Map<String, String> inMapping) {
    	this.inMapping = new LinkedList<DataAssociation>();
    	for(Map.Entry<String, String> entry : inMapping.entrySet()) {
    		addInMapping(entry.getKey(), entry.getValue());
    	}
    }

    public String getInMapping(String parameterName) {
    	return getInMappings().get(parameterName);
    }
    
    public Map<String, String> getInMappings() {
    	Map<String,String> in = new HashMap<String, String>(); 
    	for(DataAssociation a : inMapping) {
    		if(a.getSources().size() ==1 && (a.getAssignments() == null || a.getAssignments().size()==0) && a.getTransformation() == null) {
    			in.put(a.getTarget(), a.getSources().get(0));
    		}
    	}
    	return in;
    }

    public void addInAssociation(DataAssociation dataAssociation) {
        inMapping.add(dataAssociation);
    }

    public List<DataAssociation> getInAssociations() {
        return Collections.unmodifiableList(inMapping);
    }
    
    public void addOutMapping(String parameterName, String variableName) {
    	outMapping.add(new DataAssociation(parameterName, variableName, null, null));
    }

    public void setOutMappings(Map<String, String> outMapping) {
    	this.outMapping = new LinkedList<DataAssociation>();
    	for(Map.Entry<String, String> entry : outMapping.entrySet()) {
    		addOutMapping(entry.getKey(), entry.getValue());
    	}
    }

    public String getOutMapping(String parameterName) {
    	return getOutMappings().get(parameterName);
    }
    
    public Map<String, String> getOutMappings() {
    	Map<String,String> out = new HashMap<String, String>(); 
    	for(DataAssociation a : outMapping) {
    		if(a.getSources().size() ==1 && (a.getAssignments() == null || a.getAssignments().size()==0) && a.getTransformation() == null) {
    			out.put(a.getSources().get(0), a.getTarget());
    		}
    	}
    	return out;
    }
    
    public void addOutAssociation(DataAssociation dataAssociation) {
        outMapping.add(dataAssociation);
    }

    public List<DataAssociation> getOutAssociations() {
        return Collections.unmodifiableList(outMapping);
    }

    public boolean isWaitForCompletion() {
        return waitForCompletion;
    }

    public void setWaitForCompletion(boolean waitForCompletion) {
        this.waitForCompletion = waitForCompletion;
    }

    public void validateAddIncomingConnection(final String type, final Connection connection) {
        super.validateAddIncomingConnection(type, connection);
        if (!org.jbpm.workflow.core.Node.CONNECTION_DEFAULT_TYPE.equals(type)) {
            throw new IllegalArgumentException(
                "This type of node only accepts default incoming connection type!");
        }
        if (getFrom() != null && System.getProperty("jbpm.enable.multi.con") == null) {
            throw new IllegalArgumentException(
                 "This type of node cannot have more than one incoming connection!");
        }
    }

    public void validateAddOutgoingConnection(final String type, final Connection connection) {
        super.validateAddOutgoingConnection(type, connection);
        if (!org.jbpm.workflow.core.Node.CONNECTION_DEFAULT_TYPE.equals(type)) {
            throw new IllegalArgumentException(
                "This type of node only accepts default outgoing connection type!");
        }
        if (getTo() != null && System.getProperty("jbpm.enable.multi.con") == null) {
            throw new IllegalArgumentException(
              "This type of node cannot have more than one outgoing connection!");
        }
    }
    
    public List<Context> getContexts(String contextType) {
        return ((ContextContainer)this.getNodeContainer()).getContexts(contextType);
    }
    
    public void addContext(Context context) {
        ((ContextContainer)this.getNodeContainer()).addContext(context);
        ((AbstractContext) context).setContextContainer(this);
    }
    
    public Context getContext(String contextType, long id) {
        return ((ContextContainer)this.getNodeContainer()).getContext(contextType, id);
    }

    public void setDefaultContext(Context context) {
        ((ContextContainer)this.getNodeContainer()).setDefaultContext(context);
    }
    
    public Context getDefaultContext(String contextType) {
        return ((ContextContainer)this.getNodeContainer()).getDefaultContext(contextType);
    }
    
}
