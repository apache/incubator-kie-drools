package org.kie.api.fluent;

public interface DynamicNodeBuilder<T extends NodeContainerBuilder<T, ?>> extends NodeContainerBuilder<DynamicNodeBuilder<T>, T>, CompositeNodeOperations<DynamicNodeBuilder<T>, T> {

    DynamicNodeBuilder<T> autoComplete(boolean autoComplete);
}
