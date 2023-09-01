package org.kie.api.fluent;

interface TimerOperations<T extends NodeBuilder<T, P>, P extends NodeContainerBuilder<P, ?>> {

    T timer(String delay, String period, Dialect dialect, String action);
}
