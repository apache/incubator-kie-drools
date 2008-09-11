package org.drools.knowledge.definitions.process;

import java.util.List;
import java.util.Map;

public interface Node {

    long getId();

    String getName();

    Map<String, List<Connection>> getIncomingConnections();

    Map<String, List<Connection>> getOutgoingConnections();

    NodeContainer getNodeContainer();
    
}
