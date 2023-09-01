package org.kie.api.fluent;

public interface FaultNodeBuilder<T extends NodeContainerBuilder<T, ?>> extends NodeBuilder<FaultNodeBuilder<T>, T> {

    FaultNodeBuilder<T> setFaultVariable(String faultVariable);

    FaultNodeBuilder<T> setFaultName(String faultName);
}
