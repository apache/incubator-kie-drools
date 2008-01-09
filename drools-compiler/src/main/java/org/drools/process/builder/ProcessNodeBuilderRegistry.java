package org.drools.process.builder;

import java.util.HashMap;
import java.util.Map;

import org.drools.workflow.core.Node;
import org.drools.workflow.core.node.ActionNode;
import org.drools.workflow.core.node.Split;

public class ProcessNodeBuilderRegistry {
    private Map<Class< ? extends Node>, ProcessNodeBuilder> registry;

    public ProcessNodeBuilderRegistry() {
        this.registry = new HashMap<Class< ? extends Node>, ProcessNodeBuilder>();

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
