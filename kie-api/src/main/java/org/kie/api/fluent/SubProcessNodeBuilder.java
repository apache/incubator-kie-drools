package org.kie.api.fluent;

public interface SubProcessNodeBuilder<T extends NodeContainerBuilder<T, ?>> extends NodeBuilder<SubProcessNodeBuilder<T>, T>, HumanNodeOperations<SubProcessNodeBuilder<T>, T> {

    SubProcessNodeBuilder<T> processId(final String processId);

    SubProcessNodeBuilder<T> waitForCompletion(boolean waitForCompletion);

    SubProcessNodeBuilder<T> inMapping(String parameterName, String variableName);

    SubProcessNodeBuilder<T> outMapping(String parameterName, String variableName);

    SubProcessNodeBuilder<T> independent(boolean independent);

}
