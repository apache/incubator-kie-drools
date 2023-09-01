package org.kie.api.fluent;


public interface EndNodeBuilder<T extends NodeContainerBuilder<T, ?>> extends NodeBuilder<EndNodeBuilder<T>, T> {

    EndNodeBuilder<T> terminate(boolean terminate);

}
