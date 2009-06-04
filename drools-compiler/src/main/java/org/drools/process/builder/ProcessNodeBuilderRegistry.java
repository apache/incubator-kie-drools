package org.drools.process.builder;

import java.util.HashMap;
import java.util.Map;

import org.drools.definition.process.Node;
import org.drools.workflow.core.node.ActionNode;
import org.drools.workflow.core.node.EndNode;
import org.drools.workflow.core.node.FaultNode;
import org.drools.workflow.core.node.HumanTaskNode;
import org.drools.workflow.core.node.MilestoneNode;
import org.drools.workflow.core.node.RuleSetNode;
import org.drools.workflow.core.node.Split;
import org.drools.workflow.core.node.StartNode;
import org.drools.workflow.core.node.StateNode;
import org.drools.workflow.core.node.SubProcessNode;
import org.drools.workflow.core.node.TimerNode;
import org.drools.workflow.core.node.WorkItemNode;

public class ProcessNodeBuilderRegistry {
    private Map<Class< ? extends Node>, ProcessNodeBuilder> registry;

    public ProcessNodeBuilderRegistry() {
        this.registry = new HashMap<Class< ? extends Node>, ProcessNodeBuilder>();

        register( StartNode.class,
                  new ExtendedNodeBuilder() );
        register( EndNode.class,
                  new ExtendedNodeBuilder() );
        register( MilestoneNode.class,
                  new EventBasedNodeBuilder() );
        register( StateNode.class,
                  new EventBasedNodeBuilder() );
        register( RuleSetNode.class,
                  new EventBasedNodeBuilder() );
        register( SubProcessNode.class,
                  new EventBasedNodeBuilder() );
        register( HumanTaskNode.class,
                  new EventBasedNodeBuilder() );
        register( WorkItemNode.class,
                  new EventBasedNodeBuilder() );
        register( FaultNode.class,
                  new ExtendedNodeBuilder() );
        register( TimerNode.class,
                  new ExtendedNodeBuilder() );
        register( ActionNode.class,
                  new ActionNodeBuilder() );
        register( Split.class,
                  new SplitNodeBuilder() );
    }

    public void register(Class< ? extends Node> cls,
                         ProcessNodeBuilder builder) {
        this.registry.put( cls,
                           builder );
    }

    public ProcessNodeBuilder getNodeBuilder(Node node) {
        return this.registry.get( node.getClass() );
    }
}
