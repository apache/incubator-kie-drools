package org.kie.api.fluent;

public interface JoinNodeBuilder<T extends NodeContainerBuilder<T, ?>> extends NodeBuilder<JoinNodeBuilder<T>, T> {

    JoinNodeBuilder<T> type(int type);

    JoinNodeBuilder<T> type(String type);
}
