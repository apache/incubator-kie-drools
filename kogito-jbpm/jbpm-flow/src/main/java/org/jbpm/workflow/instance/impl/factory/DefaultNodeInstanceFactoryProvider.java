/*
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
package org.jbpm.workflow.instance.impl.factory;

import java.util.ArrayList;
import java.util.List;

import org.jbpm.workflow.core.node.ActionNode;
import org.jbpm.workflow.core.node.AsyncEventNode;
import org.jbpm.workflow.core.node.AsyncEventNodeInstance;
import org.jbpm.workflow.core.node.BoundaryEventNode;
import org.jbpm.workflow.core.node.CatchLinkNode;
import org.jbpm.workflow.core.node.CompositeContextNode;
import org.jbpm.workflow.core.node.CompositeNode;
import org.jbpm.workflow.core.node.DynamicNode;
import org.jbpm.workflow.core.node.EndNode;
import org.jbpm.workflow.core.node.EventNode;
import org.jbpm.workflow.core.node.EventSubProcessNode;
import org.jbpm.workflow.core.node.FaultNode;
import org.jbpm.workflow.core.node.ForEachNode;
import org.jbpm.workflow.core.node.HumanTaskNode;
import org.jbpm.workflow.core.node.Join;
import org.jbpm.workflow.core.node.MilestoneNode;
import org.jbpm.workflow.core.node.RuleSetNode;
import org.jbpm.workflow.core.node.Split;
import org.jbpm.workflow.core.node.StartNode;
import org.jbpm.workflow.core.node.StateNode;
import org.jbpm.workflow.core.node.SubProcessNode;
import org.jbpm.workflow.core.node.ThrowLinkNode;
import org.jbpm.workflow.core.node.TimerNode;
import org.jbpm.workflow.core.node.WorkItemNode;
import org.jbpm.workflow.instance.impl.NodeInstanceFactory;
import org.jbpm.workflow.instance.impl.NodeInstanceFactoryProvider;
import org.jbpm.workflow.instance.node.ActionNodeInstance;
import org.jbpm.workflow.instance.node.BoundaryEventNodeInstance;
import org.jbpm.workflow.instance.node.CatchLinkNodeInstance;
import org.jbpm.workflow.instance.node.CompositeContextNodeInstance;
import org.jbpm.workflow.instance.node.CompositeNodeInstance;
import org.jbpm.workflow.instance.node.DynamicNodeInstance;
import org.jbpm.workflow.instance.node.EndNodeInstance;
import org.jbpm.workflow.instance.node.EventNodeInstance;
import org.jbpm.workflow.instance.node.EventSubProcessNodeInstance;
import org.jbpm.workflow.instance.node.FaultNodeInstance;
import org.jbpm.workflow.instance.node.ForEachNodeInstance;
import org.jbpm.workflow.instance.node.HumanTaskNodeInstance;
import org.jbpm.workflow.instance.node.JoinInstance;
import org.jbpm.workflow.instance.node.MilestoneNodeInstance;
import org.jbpm.workflow.instance.node.RuleSetNodeInstance;
import org.jbpm.workflow.instance.node.SplitInstance;
import org.jbpm.workflow.instance.node.StartNodeInstance;
import org.jbpm.workflow.instance.node.StateNodeInstance;
import org.jbpm.workflow.instance.node.SubProcessNodeInstance;
import org.jbpm.workflow.instance.node.ThrowLinkNodeInstance;
import org.jbpm.workflow.instance.node.TimerNodeInstance;
import org.jbpm.workflow.instance.node.WorkItemNodeInstance;

public class DefaultNodeInstanceFactoryProvider implements NodeInstanceFactoryProvider {

    @Override
    public List<NodeInstanceFactory> provide() {
        List<NodeInstanceFactory> nodeInstanceFactoryList = new ArrayList<>();
        nodeInstanceFactoryList.add(new DefaultNodeInstanceFactory(RuleSetNode.class, RuleSetNodeInstance::new));
        nodeInstanceFactoryList.add(new DefaultNodeInstanceFactory(Split.class, SplitInstance::new));
        nodeInstanceFactoryList.add(new SingletonNodeInstanceFactory(Join.class, JoinInstance::new));
        nodeInstanceFactoryList.add(new DefaultNodeInstanceFactory(StartNode.class, StartNodeInstance::new));
        nodeInstanceFactoryList.add(new DefaultNodeInstanceFactory(EndNode.class, EndNodeInstance::new));
        nodeInstanceFactoryList.add(new DefaultNodeInstanceFactory(MilestoneNode.class, MilestoneNodeInstance::new));
        nodeInstanceFactoryList.add(new DefaultNodeInstanceFactory(SubProcessNode.class, SubProcessNodeInstance::new));
        nodeInstanceFactoryList.add(new DefaultNodeInstanceFactory(ActionNode.class, ActionNodeInstance::new));
        nodeInstanceFactoryList.add(new DefaultNodeInstanceFactory(WorkItemNode.class, WorkItemNodeInstance::new));
        nodeInstanceFactoryList.add(new DefaultNodeInstanceFactory(TimerNode.class, TimerNodeInstance::new));
        nodeInstanceFactoryList.add(new DefaultNodeInstanceFactory(FaultNode.class, FaultNodeInstance::new));
        nodeInstanceFactoryList.add(new DefaultNodeInstanceFactory(EventSubProcessNode.class, EventSubProcessNodeInstance::new));
        nodeInstanceFactoryList.add(new DefaultNodeInstanceFactory(CompositeNode.class, CompositeNodeInstance::new));
        nodeInstanceFactoryList.add(new DefaultNodeInstanceFactory(CompositeContextNode.class, CompositeContextNodeInstance::new));
        nodeInstanceFactoryList.add(new DefaultNodeInstanceFactory(HumanTaskNode.class, HumanTaskNodeInstance::new));
        nodeInstanceFactoryList.add(new DefaultNodeInstanceFactory(ForEachNode.class, ForEachNodeInstance::new));
        nodeInstanceFactoryList.add(new DefaultNodeInstanceFactory(EventNode.class, EventNodeInstance::new));
        nodeInstanceFactoryList.add(new DefaultNodeInstanceFactory(StateNode.class, StateNodeInstance::new));
        nodeInstanceFactoryList.add(new DefaultNodeInstanceFactory(DynamicNode.class, DynamicNodeInstance::new));
        nodeInstanceFactoryList.add(new DefaultNodeInstanceFactory(BoundaryEventNode.class, BoundaryEventNodeInstance::new));
        nodeInstanceFactoryList.add(new DefaultNodeInstanceFactory(CatchLinkNode.class, CatchLinkNodeInstance::new));
        nodeInstanceFactoryList.add(new DefaultNodeInstanceFactory(ThrowLinkNode.class, ThrowLinkNodeInstance::new));
        nodeInstanceFactoryList.add(new DefaultNodeInstanceFactory(AsyncEventNode.class, AsyncEventNodeInstance::new));
        return nodeInstanceFactoryList;
    }

}
