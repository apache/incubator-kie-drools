package org.kie.api.fluent;

/**
 * Include operations to define a container node.<br> 
 * As it name indicates, a container node contains nodes (a process is also a container node), so this class defines all methods to create children nodes.<br>
 * A container node also holds variables, exception handlers and establish connections between nodes.  
 * @param <T> Concrete container node
 * @param <P> Parent container node
 */
public interface NodeContainerBuilder<T extends NodeContainerBuilder<T, P>, P extends NodeContainerBuilder<P, ?>> extends NodeBuilder<T, P> {

    StartNodeBuilder<T> startNode(long id);

    EndNodeBuilder<T> endNode(long id);

    ActionNodeBuilder<T> actionNode(long id);

    MilestoneNodeBuilder<T> milestoneNode(long id);

    TimerNodeBuilder<T> timerNode(long id);

    HumanTaskNodeBuilder<T> humanTaskNode(long id);

    SubProcessNodeBuilder<T> subProcessNode(long id);

    SplitNodeBuilder<T> splitNode(long id);

    JoinNodeBuilder<T> joinNode(long id);

    RuleSetNodeBuilder<T> ruleSetNode(long id);

    FaultNodeBuilder<T> faultNode(long id);

    EventNodeBuilder<T> eventNode(long id);

    BoundaryEventNodeBuilder<T> boundaryEventNode(long id);

    CompositeNodeBuilder<T> compositeNode(long id);

    ForEachNodeBuilder<T> forEachNode(long id);

    DynamicNodeBuilder<T> dynamicNode(long id);

    WorkItemNodeBuilder<T> workItemNode(long id);

    T exceptionHandler(Class<? extends Throwable> exceptionClass, Dialect dialect, String code);

    T connection(long fromId, long toId);

    /**
     * Adds a variable to this container 
     */
    <V> T variable(Variable<V> variable);
}
