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

import java.util.*;

import org.jbpm.process.core.context.variable.Mappable;
import org.jbpm.workflow.core.impl.ExtendedNodeImpl;
import org.kie.definition.process.Connection;

/**
 * Default implementation of a start node.
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public class StartNode extends ExtendedNodeImpl implements Mappable {

	private static final String[] EVENT_TYPES =
		new String[] { EVENT_NODE_EXIT };
	
    private static final long serialVersionUID = 510l;
    
    private List<Trigger> triggers;
    private boolean isInterrupting;
    
    private List<DataAssociation> outMapping = new LinkedList<DataAssociation>();

	public void addTrigger(Trigger trigger) {
		if (triggers == null) {
			triggers = new ArrayList<Trigger>();
		}
		triggers.add(trigger);
	}
	
	public void removeTrigger(Trigger trigger) {
		if (triggers != null) {
			triggers.remove(trigger);
		}
	}
	
	public List<Trigger> getTriggers() {
		return triggers;
	}
		
	public void setTriggers(List<Trigger> triggers) {
		this.triggers = triggers;
	}
		
	public String[] getActionTypes() {
		return EVENT_TYPES;
	}
	
    public void validateAddIncomingConnection(final String type, final Connection connection) {
        throw new UnsupportedOperationException(
            "A start node does not have an incoming connection!");
    }

    public void validateRemoveIncomingConnection(final String type, final Connection connection) {
        throw new UnsupportedOperationException(
            "A start node does not have an incoming connection!");
    }
    
    public void validateAddOutgoingConnection(final String type, final Connection connection) {
        super.validateAddOutgoingConnection(type, connection);
        if (!org.jbpm.workflow.core.Node.CONNECTION_DEFAULT_TYPE.equals(type)) {
            throw new IllegalArgumentException(
                "This type of node only accepts default outgoing connection type!");
        }
        if (getTo() != null) {
            throw new IllegalArgumentException(
                "This type of node cannot have more than one outgoing connection!");
        }
    }

    public boolean isInterrupting() {
        return isInterrupting;
    }

    public void setInterrupting(boolean isInterrupting) {
        this.isInterrupting = isInterrupting;
    }

    @Override
    public void addInMapping(String parameterName, String variableName) {
        throw new IllegalArgumentException("A start event does not support input mappings");
    }

    @Override
    public void setInMappings(Map<String, String> inMapping) {
        throw new IllegalArgumentException("A start event does not support input mappings");
    }

    @Override
    public String getInMapping(String parameterName) {
        throw new IllegalArgumentException("A start event does not support input mappings");
    }

    @Override
    public Map<String, String> getInMappings() {
        throw new IllegalArgumentException("A start event does not support input mappings");
    }

    @Override
    public void addInAssociation(DataAssociation dataAssociation) {
        throw new IllegalArgumentException("A start event does not support input mappings");
    }

    @Override
    public List<DataAssociation> getInAssociations() {
        throw new IllegalArgumentException("A start event does not support input mappings");
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
        for(DataAssociation assoc : outMapping) {
            if( assoc.getSources().size() == 1 
             && (assoc.getAssignments() == null || assoc.getAssignments().size() == 0) 
             && assoc.getTransformation() == null ) {
                out.put(assoc.getSources().get(0), assoc.getTarget());
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
    
}
