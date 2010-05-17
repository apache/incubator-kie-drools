package org.drools.runtime.process;

import java.util.Collection;

public interface NodeInstanceContainer {

    Collection<NodeInstance> getNodeInstances();
    
    NodeInstance getNodeInstance(long nodeInstanceId);

}
