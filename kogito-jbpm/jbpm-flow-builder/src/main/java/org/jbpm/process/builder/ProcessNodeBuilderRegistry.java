package org.jbpm.process.builder;

import java.util.HashMap;
import java.util.Map;

import org.jbpm.workflow.core.impl.NodeImpl;
import org.jbpm.workflow.core.node.ActionNode;
import org.jbpm.workflow.core.node.CompositeContextNode;
import org.jbpm.workflow.core.node.EndNode;
import org.jbpm.workflow.core.node.FaultNode;
import org.jbpm.workflow.core.node.HumanTaskNode;
import org.jbpm.workflow.core.node.MilestoneNode;
import org.jbpm.workflow.core.node.RuleSetNode;
import org.jbpm.workflow.core.node.Split;
import org.jbpm.workflow.core.node.StartNode;
import org.jbpm.workflow.core.node.StateNode;
import org.jbpm.workflow.core.node.SubProcessNode;
import org.jbpm.workflow.core.node.TimerNode;
import org.jbpm.workflow.core.node.WorkItemNode;
import org.kie.definition.process.Node;

public class ProcessNodeBuilderRegistry {
	
	public static ProcessNodeBuilderRegistry INSTANCE = new ProcessNodeBuilderRegistry();
	
    private Map<Class< ? extends Node>, ProcessNodeBuilder> registry;
    
    public ProcessNodeBuilderRegistry() {
        this.registry = new HashMap<Class< ? extends Node>, ProcessNodeBuilder>();

        register( StartNode.class,
                  new ExtendedNodeBuilder() );
        register( EndNode.class,
                  new ExtendedNodeBuilder() );
        register( MilestoneNode.class,
                  new EventBasedNodeBuilder() );
        register( RuleSetNode.class,
                  new EventBasedNodeBuilder() );
        register( SubProcessNode.class,
                  new EventBasedNodeBuilder() );
        register( HumanTaskNode.class,
                  new WorkItemNodeBuilder() );
        register( WorkItemNode.class,
                  new WorkItemNodeBuilder() );
        register( FaultNode.class,
                  new ExtendedNodeBuilder() );
        register( TimerNode.class,
                  new ExtendedNodeBuilder() );
        register( ActionNode.class,
                  new ActionNodeBuilder() );
        register( Split.class,
                  new SplitNodeBuilder() );
        register( CompositeContextNode.class,
                  new EventBasedNodeBuilder() );
        register( StateNode.class,
                  new EventBasedNodeBuilder() );
        register( NodeImpl.class,
                new MultiConditionalSequenceFlowNodeBuilder() );
    }

    public void register(Class< ? extends Node> cls,
                         ProcessNodeBuilder builder) {
        this.registry.put( cls,
                           builder );
    }

    public ProcessNodeBuilder getNodeBuilder(Node node) {
        return this.registry.get( node.getClass() );
    }
    
    public ProcessNodeBuilder getNodeBuilder(Class< ? extends Node> cls) {
        return this.registry.get( cls );
    }
}
