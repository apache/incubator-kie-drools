package org.drools.workflow.instance.impl;

import java.util.HashMap;
import java.util.Map;

import org.drools.definition.process.Node;
import org.drools.workflow.core.node.ActionNode;
import org.drools.workflow.core.node.CompositeContextNode;
import org.drools.workflow.core.node.CompositeNode;
import org.drools.workflow.core.node.DynamicNode;
import org.drools.workflow.core.node.EndNode;
import org.drools.workflow.core.node.EventNode;
import org.drools.workflow.core.node.FaultNode;
import org.drools.workflow.core.node.ForEachNode;
import org.drools.workflow.core.node.HumanTaskNode;
import org.drools.workflow.core.node.Join;
import org.drools.workflow.core.node.MilestoneNode;
import org.drools.workflow.core.node.RuleSetNode;
import org.drools.workflow.core.node.Split;
import org.drools.workflow.core.node.StartNode;
import org.drools.workflow.core.node.StateNode;
import org.drools.workflow.core.node.SubProcessNode;
import org.drools.workflow.core.node.TimerNode;
import org.drools.workflow.core.node.WorkItemNode;
import org.drools.workflow.instance.impl.factory.CreateNewNodeFactory;
import org.drools.workflow.instance.impl.factory.ReuseNodeFactory;
import org.drools.workflow.instance.node.ActionNodeInstance;
import org.drools.workflow.instance.node.CompositeContextNodeInstance;
import org.drools.workflow.instance.node.CompositeNodeInstance;
import org.drools.workflow.instance.node.DynamicNodeInstance;
import org.drools.workflow.instance.node.EndNodeInstance;
import org.drools.workflow.instance.node.EventNodeInstance;
import org.drools.workflow.instance.node.FaultNodeInstance;
import org.drools.workflow.instance.node.ForEachNodeInstance;
import org.drools.workflow.instance.node.HumanTaskNodeInstance;
import org.drools.workflow.instance.node.JoinInstance;
import org.drools.workflow.instance.node.MilestoneNodeInstance;
import org.drools.workflow.instance.node.RuleSetNodeInstance;
import org.drools.workflow.instance.node.SplitInstance;
import org.drools.workflow.instance.node.StartNodeInstance;
import org.drools.workflow.instance.node.StateNodeInstance;
import org.drools.workflow.instance.node.SubProcessNodeInstance;
import org.drools.workflow.instance.node.TimerNodeInstance;
import org.drools.workflow.instance.node.WorkItemNodeInstance;

public class NodeInstanceFactoryRegistry {
    public static final NodeInstanceFactoryRegistry          instance = new NodeInstanceFactoryRegistry();

    private Map<Class< ? extends Node>, NodeInstanceFactory> registry;

    public NodeInstanceFactoryRegistry() {
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
