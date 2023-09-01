package org.kie.api.fluent;


public interface CompositeNodeOperations<T extends NodeContainerBuilder<T, P>, P extends NodeContainerBuilder<P, ?>> {

    T linkIncomingConnections(long nodeId);

    T linkOutgoingConnections(long nodeId);
}
