package org.kie.api.fluent;

public interface ActionNodeBuilder<T extends NodeContainerBuilder<T, ?>> extends NodeBuilder<ActionNodeBuilder<T>, T> {

    ActionNodeBuilder<T> action(Dialect dialect, String code);
}
