package org.kie.api.fluent;

public interface HumanTaskNodeBuilder<T extends NodeContainerBuilder<T, ?>> extends NodeBuilder<HumanTaskNodeBuilder<T>, T>, HumanNodeOperations<HumanTaskNodeBuilder<T>, T> {

    HumanTaskNodeBuilder<T> taskName(String taskName);

    HumanTaskNodeBuilder<T> actorId(String actorId);

    HumanTaskNodeBuilder<T> priority(String priority);

    HumanTaskNodeBuilder<T> comment(String comment);

    HumanTaskNodeBuilder<T> skippable(boolean skippable);

    HumanTaskNodeBuilder<T> content(String content);

    HumanTaskNodeBuilder<T> inMapping(String parameterName, String variableName);

    HumanTaskNodeBuilder<T> outMapping(String parameterName, String variableName);

    HumanTaskNodeBuilder<T> waitForCompletion(boolean waitForCompletion);

    HumanTaskNodeBuilder<T> swimlane(String swimlane);

    HumanTaskNodeBuilder<T> workParameter(String name, Object value);
}
