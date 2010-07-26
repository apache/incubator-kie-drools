/**
 * Copyright 2010 JBoss Inc
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

package org.drools.event.process.impl;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.WorkingMemory;
import org.drools.event.RuleFlowNodeTriggeredEvent;
import org.drools.event.process.ProcessNodeEvent;
import org.drools.runtime.process.NodeInstance;

public class ProcessNodeEventImpl extends ProcessEventImpl implements ProcessNodeEvent {

    private NodeInstance nodeInstance;

    public ProcessNodeEventImpl(RuleFlowNodeTriggeredEvent event, WorkingMemory workingMemory) {
        super(event, workingMemory);
        nodeInstance = event.getRuleFlowNodeInstance();
    }
    
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal( out );
        out.writeObject( nodeInstance );
    }
    
    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        super.readExternal( in );
        this.nodeInstance = (NodeInstance) in.readObject();
    }

	public NodeInstance getNodeInstance() {
		return nodeInstance;
	}

	@Override
	public String toString() {
		return "==>[ProcessNodeEventImpl: getNodeInstance()=" + getNodeInstance() + ", getProcessInstance()="
				+ getProcessInstance() + ", getKnowledgeRuntime()=" + getKnowledgeRuntime() + "]";
	}
}
