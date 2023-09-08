/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
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
