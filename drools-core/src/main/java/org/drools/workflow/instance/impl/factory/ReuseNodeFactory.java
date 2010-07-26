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

package org.drools.workflow.instance.impl.factory;

import org.drools.definition.process.Node;
import org.drools.runtime.process.NodeInstance;
import org.drools.runtime.process.NodeInstanceContainer;
import org.drools.workflow.instance.WorkflowProcessInstance;
import org.drools.workflow.instance.impl.NodeInstanceFactory;
import org.drools.workflow.instance.impl.NodeInstanceImpl;

public class ReuseNodeFactory implements NodeInstanceFactory {
    
    public final Class<? extends NodeInstance> cls;
    
    public ReuseNodeFactory(Class<? extends NodeInstance> cls){
        this.cls = cls;
    }

	public NodeInstance getNodeInstance(Node node, WorkflowProcessInstance processInstance, NodeInstanceContainer nodeInstanceContainer) {    	
        NodeInstance result = ((org.drools.workflow.instance.NodeInstanceContainer)
    		nodeInstanceContainer).getFirstNodeInstance( node.getId() );
        if (result != null) {
            return result;
        }
        try {
            NodeInstanceImpl nodeInstance = (NodeInstanceImpl) cls.newInstance();
            nodeInstance.setNodeId(node.getId());
            nodeInstance.setNodeInstanceContainer(nodeInstanceContainer);
            nodeInstance.setProcessInstance(processInstance);
            return nodeInstance;
        } catch (Exception e) {
            throw new RuntimeException("Unable to instantiate node '"
                + this.cls.getName() + "': " + e.getMessage());
        }
	}

}
