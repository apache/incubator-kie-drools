package org.kie.api.fluent;

public interface MilestoneNodeBuilder<T extends NodeContainerBuilder<T, ?>> extends NodeBuilder<MilestoneNodeBuilder<T>, T>, HumanNodeOperations<MilestoneNodeBuilder<T>, T> {

    MilestoneNodeBuilder<T> constraint(String constraint);
}

