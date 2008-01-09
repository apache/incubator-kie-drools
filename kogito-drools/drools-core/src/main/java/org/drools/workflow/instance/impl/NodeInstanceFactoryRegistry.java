package org.drools.workflow.instance.impl;

import java.util.HashMap;
import java.util.Map;

import org.drools.workflow.core.Node;
import org.drools.workflow.core.node.ActionNode;
import org.drools.workflow.core.node.EndNode;
import org.drools.workflow.core.node.Join;
import org.drools.workflow.core.node.MilestoneNode;
import org.drools.workflow.core.node.RuleSetNode;
import org.drools.workflow.core.node.Split;
import org.drools.workflow.core.node.StartNode;
import org.drools.workflow.core.node.SubProcessNode;
import org.drools.workflow.core.node.WorkItemNode;
import org.drools.workflow.instance.impl.factory.CreateNewNodeFactory;
import org.drools.workflow.instance.impl.factory.ReuseNodeFactory;
import org.drools.workflow.instance.node.ActionNodeInstance;
import org.drools.workflow.instance.node.EndNodeInstance;
import org.drools.workflow.instance.node.JoinInstance;
import org.drools.workflow.instance.node.MilestoneNodeInstance;
import org.drools.workflow.instance.node.RuleSetNodeInstance;
import org.drools.workflow.instance.node.SplitInstance;
import org.drools.workflow.instance.node.StartNodeInstance;
import org.drools.workflow.instance.node.SubProcessNodeInstance;
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
    }

    public void register(Class< ? extends Node> cls,
                         NodeInstanceFactory factory) {
        this.registry.put( cls,
                           factory );
    }

    public NodeInstanceFactory getProcessNodeInstanceFactory(Node node) {
        return this.registry.get( node.getClass() );
    }
}
