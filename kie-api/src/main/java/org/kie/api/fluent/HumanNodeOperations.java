package org.kie.api.fluent;


interface HumanNodeOperations<T extends NodeBuilder<T, P>, P extends NodeContainerBuilder<P, ?>> extends TimerOperations<T, P> {

    T onEntryAction(Dialect dialect, String action);

    T onExitAction(Dialect dialect, String action);
}
