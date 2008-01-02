package org.drools.ruleflow.instance.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.drools.ruleflow.core.Node;
import org.drools.ruleflow.core.WorkItemNode;
import org.drools.ruleflow.core.impl.ActionNodeImpl;
import org.drools.ruleflow.core.impl.EndNodeImpl;
import org.drools.ruleflow.core.impl.JoinImpl;
import org.drools.ruleflow.core.impl.MilestoneNodeImpl;
import org.drools.ruleflow.core.impl.RuleSetNodeImpl;
import org.drools.ruleflow.core.impl.SplitImpl;
import org.drools.ruleflow.core.impl.StartNodeImpl;
import org.drools.ruleflow.core.impl.SubFlowNodeImpl;
import org.drools.ruleflow.instance.impl.factories.CreateNewNodeFactory;
import org.drools.ruleflow.instance.impl.factories.ReuseNodeFactory;
import org.drools.ruleflow.instance.impl.factories.RuleSetNodeFactory;
import org.drools.util.ConfFileUtils;
import org.mvel.MVEL;

public class ProcessNodeInstanceFactoryRegistry {
    public static final ProcessNodeInstanceFactoryRegistry          instance = new ProcessNodeInstanceFactoryRegistry();

    private Map<Class< ? extends Node>, ProcessNodeInstanceFactory> registry;

    public ProcessNodeInstanceFactoryRegistry() {
        this.registry = new HashMap<Class< ? extends Node>, ProcessNodeInstanceFactory>();

        // hard wired nodes:
        register( RuleSetNodeImpl.class,
                  new RuleSetNodeFactory() );
        register( SplitImpl.class,
                  new ReuseNodeFactory( RuleFlowSplitInstanceImpl.class ) );
        register( JoinImpl.class,
                  new ReuseNodeFactory( RuleFlowJoinInstanceImpl.class ) );
        register( StartNodeImpl.class,
                  new ReuseNodeFactory( StartNodeInstanceImpl.class ) );
        register( EndNodeImpl.class,
                  new CreateNewNodeFactory( EndNodeInstanceImpl.class ) );
        register( MilestoneNodeImpl.class,
                  new CreateNewNodeFactory( MilestoneNodeInstanceImpl.class ) );
        register( SubFlowNodeImpl.class,
                  new CreateNewNodeFactory( SubFlowNodeInstanceImpl.class ) );
        register( ActionNodeImpl.class,
                  new CreateNewNodeFactory( ActionNodeInstanceImpl.class ) );
        register( WorkItemNode.class,
                  new CreateNewNodeFactory( TaskNodeInstanceImpl.class ) );
    }

    public void register(Class< ? extends Node> cls,
                         ProcessNodeInstanceFactory factory) {
        this.registry.put( cls,
                           factory );
    }

    public ProcessNodeInstanceFactory getRuleFlowNodeFactory(Node node) {
        return this.registry.get( node.getClass() );
    }
}
