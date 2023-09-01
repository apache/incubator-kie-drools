package org.kie.api.fluent;

public interface TimerNodeBuilder<T extends NodeContainerBuilder<T, ?>> extends NodeBuilder<TimerNodeBuilder<T>, T> {

    TimerNodeBuilder<T> delay(String delay);

    TimerNodeBuilder<T> period(String period);
}
