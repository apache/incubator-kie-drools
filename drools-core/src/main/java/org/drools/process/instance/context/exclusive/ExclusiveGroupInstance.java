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
