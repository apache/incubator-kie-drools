package org.kie.api.fluent;

public interface CompositeNodeBuilder<T extends NodeContainerBuilder<T, ?>> extends NodeContainerBuilder<CompositeNodeBuilder<T>, T>, CompositeNodeOperations<CompositeNodeBuilder<T>, T> {
}
