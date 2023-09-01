package org.kie.api.fluent;

public interface BoundaryEventNodeBuilder<T extends NodeContainerBuilder<T, ?>> extends EventNodeOperations<BoundaryEventNodeBuilder<T>, T>, NodeBuilder<BoundaryEventNodeBuilder<T>, T> {

    BoundaryEventNodeBuilder<T> attachedTo(long attachedToId);

    BoundaryEventNodeBuilder<T> eventType(String prefix, String Suffix);

    BoundaryEventNodeBuilder<T> timeCycle(String timeCycle);

    BoundaryEventNodeBuilder<T> timeCycle(String timeCycle, Dialect dialect);

    BoundaryEventNodeBuilder<T> timeDuration(String timeDuration);

    BoundaryEventNodeBuilder<T> cancelActivity(boolean cancelActivity);
}
