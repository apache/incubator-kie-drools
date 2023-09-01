package org.kie.api.fluent;

public interface SplitNodeBuilder<T extends NodeContainerBuilder<T, ?>> extends NodeBuilder<SplitNodeBuilder<T>, T> {

    SplitNodeBuilder<T> type(int type);

    SplitNodeBuilder<T> constraint(long toNodeId, String name, String type, Dialect dialect, String constraint, int priority);

}
