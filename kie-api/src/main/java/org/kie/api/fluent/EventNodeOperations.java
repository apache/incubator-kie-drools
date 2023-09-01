package org.kie.api.fluent;

import java.util.function.UnaryOperator;

interface EventNodeOperations<T extends NodeBuilder<T, P>, P extends NodeContainerBuilder<P, ?>> {

    T eventType(String eventType);

    T eventTransformer(UnaryOperator<Object> function);

    T scope(String scope);

    T variableName(String name);
}
