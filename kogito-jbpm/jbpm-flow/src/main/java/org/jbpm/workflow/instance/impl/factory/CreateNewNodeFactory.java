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

package org.jbpm.workflow.instance.impl.factory;

import org.kie.api.definition.process.Node;
import org.kie.api.runtime.process.NodeInstance;
import org.kie.api.runtime.process.NodeInstanceContainer;
import org.jbpm.workflow.instance.WorkflowProcessInstance;
import org.jbpm.workflow.instance.impl.NodeInstanceFactory;
import org.jbpm.workflow.instance.impl.NodeInstanceImpl;

public class CreateNewNodeFactory implements NodeInstanceFactory {
    
    public final Class<? extends NodeInstance> cls;
    
    public CreateNewNodeFactory(Class<? extends NodeInstance> cls){
        this.cls = cls;
    }
    
    public NodeInstance getNodeInstance(Node node, WorkflowProcessInstance processInstance, NodeInstanceContainer nodeInstanceContainer) {     
        try {
            NodeInstanceImpl nodeInstance = (NodeInstanceImpl) this.cls.newInstance();
            nodeInstance.setNodeId(node.getId());
            nodeInstance.setNodeInstanceContainer(nodeInstanceContainer);
            nodeInstance.setProcessInstance(processInstance);
            String uniqueId = (String) node.getMetaData().get("UniqueId");
            assert uniqueId != null : node.getClass().getSimpleName() + " [" + node.getName() + "] does not have a unique id.";
            if (uniqueId == null) {
                uniqueId = node.getId()+"";
            }
            nodeInstance.setMetaData("UniqueId", uniqueId);
            int level = ((org.jbpm.workflow.instance.NodeInstanceContainer)nodeInstanceContainer).getLevelForNode(uniqueId);
            nodeInstance.setLevel(level);
            return nodeInstance;
        } catch (Exception e) {
        	e.printStackTrace();
            throw new RuntimeException("Unable to instantiate node: '"
                + this.cls.getName() + "':" + e.getMessage());
        }
	}

}
