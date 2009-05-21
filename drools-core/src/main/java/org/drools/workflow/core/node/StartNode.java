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

import java.util.ArrayList;
import java.util.List;

import org.drools.definition.process.Connection;

/**
 * Default implementation of a start node.
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public class StartNode extends SequenceNode {

    private static final long serialVersionUID = 400L;
    
    private List<Trigger> triggers;

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
		
    public void validateAddIncomingConnection(final String type, final Connection connection) {
        throw new UnsupportedOperationException(
            "A start node does not have an incoming connection!");
    }

    public void validateRemoveIncomingConnection(final String type, final Connection connection) {
        throw new UnsupportedOperationException(
            "A start node does not have an incoming connection!");
    }
    
}
