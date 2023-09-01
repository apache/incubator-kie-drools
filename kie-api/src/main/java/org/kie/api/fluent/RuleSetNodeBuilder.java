package org.kie.api.fluent;

public interface RuleSetNodeBuilder<T extends NodeContainerBuilder<T, ?>> extends NodeBuilder<RuleSetNodeBuilder<T>, T>, TimerOperations<RuleSetNodeBuilder<T>, T> {

    RuleSetNodeBuilder<T> ruleFlowGroup(String ruleFlowGroup);
}
