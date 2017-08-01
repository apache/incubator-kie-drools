/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.workflow.instance.impl;

import java.util.HashMap;
import java.util.Map;

import org.jbpm.workflow.core.node.ActionNode;
import org.jbpm.workflow.core.node.AsyncEventNode;
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
import org.jbpm.workflow.instance.impl.factory.CreateNewNodeFactory;
import org.jbpm.workflow.instance.impl.factory.ReuseNodeFactory;
import org.jbpm.workflow.instance.node.ActionNodeInstance;
import org.jbpm.workflow.instance.node.AsyncEventNodeInstance;
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
import org.kie.api.definition.process.Node;
import org.kie.api.runtime.Environment;

public class NodeInstanceFactoryRegistry {
	
    private static final NodeInstanceFactoryRegistry INSTANCE = new NodeInstanceFactoryRegistry();

    private Map<Class< ? extends Node>, NodeInstanceFactory> registry;
    
    public static NodeInstanceFactoryRegistry getInstance(Environment environment) {
        // allow custom NodeInstanceFactoryRegistry to be given as part of the environment - e.g simulation
        if (environment != null && environment.get("NodeInstanceFactoryRegistry") != null) {
            return (NodeInstanceFactoryRegistry) environment.get("NodeInstanceFactoryRegistry");
        }
        
        return INSTANCE;
    }

    protected NodeInstanceFactoryRegistry() {
        this.registry = new HashMap<Class< ? extends Node>, NodeInstanceFactory>();

        // hard wired nodes:
        register( RuleSetNode.class,
                  new CreateNewNodeFactory( RuleSetNodeInstance.class ) );
        register( Split.class,
                  new CreateNewNodeFactory( SplitInstance.class ) );
        register( Join.class,
                  new ReuseNodeFactory( JoinInstance.class ) );
        register( StartNode.class,
                  new CreateNewNodeFactory( StartNodeInstance.class ) );
        register( EndNode.class,
                  new CreateNewNodeFactory( EndNodeInstance.class ) );
        register( MilestoneNode.class,
                  new CreateNewNodeFactory( MilestoneNodeInstance.class ) );
        register( SubProcessNode.class,
                  new CreateNewNodeFactory( SubProcessNodeInstance.class ) );
        register( ActionNode.class,
                  new CreateNewNodeFactory( ActionNodeInstance.class ) );
        register( WorkItemNode.class,
                  new CreateNewNodeFactory( WorkItemNodeInstance.class ) );
        register( TimerNode.class,
                  new CreateNewNodeFactory( TimerNodeInstance.class ) );
        register( FaultNode.class,
                  new CreateNewNodeFactory( FaultNodeInstance.class ) );
        register(EventSubProcessNode.class, 
                  new CreateNewNodeFactory(EventSubProcessNodeInstance.class));
        register( CompositeNode.class,
                  new CreateNewNodeFactory( CompositeNodeInstance.class ) );
        register( CompositeContextNode.class,
                  new CreateNewNodeFactory( CompositeContextNodeInstance.class ) );
        register( HumanTaskNode.class,
                  new CreateNewNodeFactory( HumanTaskNodeInstance.class ) );
        register( ForEachNode.class,
                  new CreateNewNodeFactory( ForEachNodeInstance.class ) );
        register( EventNode.class,
                  new CreateNewNodeFactory( EventNodeInstance.class ) );
        register( StateNode.class,
                  new CreateNewNodeFactory( StateNodeInstance.class ) );
        register( DynamicNode.class,
                  new CreateNewNodeFactory( DynamicNodeInstance.class ) );
        register( BoundaryEventNode.class,
                new CreateNewNodeFactory( BoundaryEventNodeInstance.class ) );
        register( AsyncEventNode.class,
                new CreateNewNodeFactory( AsyncEventNodeInstance.class ) );
        
        register(CatchLinkNode.class, new CreateNewNodeFactory(
				CatchLinkNodeInstance.class));
		register(ThrowLinkNode.class, new CreateNewNodeFactory(
				ThrowLinkNodeInstance.class));
		
		
    }

    public void register(Class< ? extends Node> cls,
                         NodeInstanceFactory factory) {
        this.registry.put( cls,
                           factory );
    }

    public NodeInstanceFactory getProcessNodeInstanceFactory(Node node) {
    	Class<?> clazz = node.getClass();
        while (clazz != null) {
        	NodeInstanceFactory result = this.registry.get( clazz );
        	if (result != null) {
        		return result;
        	}
        	clazz = clazz.getSuperclass();
        }
        return null;
    }
}
