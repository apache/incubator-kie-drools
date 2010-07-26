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

package org.drools.process.instance.context.exclusive;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.drools.process.core.context.exclusive.ExclusiveGroup;
import org.drools.process.instance.context.AbstractContextInstance;
import org.drools.runtime.process.NodeInstance;

public class ExclusiveGroupInstance extends AbstractContextInstance {

    private static final long serialVersionUID = 400L;
    
    private Map<Long, NodeInstance> nodeInstances = new HashMap<Long, NodeInstance>();
    
    public String getContextType() {
        return ExclusiveGroup.EXCLUSIVE_GROUP;
    }
    
    public boolean containsNodeInstance(NodeInstance nodeInstance) {
    	return nodeInstances.containsKey(nodeInstance.getId());
    }
    
    public void addNodeInstance(NodeInstance nodeInstance) {
    	nodeInstances.put(nodeInstance.getId(), nodeInstance);
    }
    
    public Collection<NodeInstance> getNodeInstances() {
    	return nodeInstances.values();
    }
    
}
