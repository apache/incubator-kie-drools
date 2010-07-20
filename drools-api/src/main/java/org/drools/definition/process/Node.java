package org.drools.definition.process;

import java.util.List;
import java.util.Map;

public interface Node {

    long getId();

    String getName();

    Map<String, List<Connection>> getIncomingConnections();

    Map<String, List<Connection>> getOutgoingConnections();

    List<Connection> getIncomingConnections(String type);

    List<Connection> getOutgoingConnections(String type);

    NodeContainer getNodeContainer();

    Map<String, Object> getMetaData();

    @Deprecated Object getMetaData(String name);

}
