package org.kie.api.fluent;

public interface EventNodeBuilder<T extends NodeContainerBuilder<T, ?>> extends NodeBuilder<EventNodeBuilder<T>, T>, EventNodeOperations<EventNodeBuilder<T>, T> {
}

