package org.kie.api.fluent;

public interface WorkItemNodeBuilder<T extends NodeContainerBuilder<T, ?>> extends NodeBuilder<WorkItemNodeBuilder<T>, T>, HumanNodeOperations<WorkItemNodeBuilder<T>, T> {

    WorkItemNodeBuilder<T> waitForCompletion(boolean waitForCompletion);

    WorkItemNodeBuilder<T> inMapping(String parameterName, String variableName);

    WorkItemNodeBuilder<T> outMapping(String parameterName, String variableName);

    WorkItemNodeBuilder<T> workName(String name);

    WorkItemNodeBuilder<T> workParameter(String name, Object value);

    WorkItemNodeBuilder<T> workParameterDefinition(String name, Class<?> type);
}
