package org.kie.api.fluent;


public interface ForEachNodeBuilder<T extends NodeContainerBuilder<T, ?>> extends NodeContainerBuilder<ForEachNodeBuilder<T>, T>, CompositeNodeOperations<ForEachNodeBuilder<T>, T> {

    ForEachNodeBuilder<T> collectionExpression(String collectionExpression);

    ForEachNodeBuilder<T> waitForCompletion(boolean waitForCompletion);
}
