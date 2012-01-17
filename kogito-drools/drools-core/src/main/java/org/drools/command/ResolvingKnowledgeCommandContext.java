package org.drools.command;

import org.drools.KnowledgeBase;
import org.drools.builder.KnowledgeBuilder;
import org.drools.command.impl.KnowledgeCommandContext;
import org.drools.runtime.ExecutionResults;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.WorkItemManager;
import org.drools.runtime.rule.WorkingMemoryEntryPoint;

public class ResolvingKnowledgeCommandContext implements KnowledgeCommandContext {
    
    private Context                  context;

    public ResolvingKnowledgeCommandContext(Context context) {
        super();
        this.context = context;
    }
    

    public KnowledgeBuilder getKnowledgeBuilder() {
        return ( KnowledgeBuilder ) context.get( KnowledgeBuilder.class.getName() );
    }

    public void setKnowledgeBuilder(KnowledgeBuilder kbuilder) {
        context.set( KnowledgeBuilder.class.getName(), kbuilder );
    }

    public KnowledgeBase getKnowledgeBase() {
        return ( KnowledgeBase ) context.get( KnowledgeBase.class.getName() );
    }

    public StatefulKnowledgeSession getStatefulKnowledgesession() {
        return ( StatefulKnowledgeSession ) context.get( StatefulKnowledgeSession.class.getName() );
    }

    public WorkItemManager getWorkItemManager() {
        return ( WorkItemManager ) context.get( WorkItemManager.class.getName() );
    }

    public ExecutionResults getExecutionResults() {
        return ( ExecutionResults ) context.get( ExecutionResults.class.getName() );
    }

    public WorkingMemoryEntryPoint getWorkingMemoryEntryPoint() {
        return ( WorkingMemoryEntryPoint ) context.get( WorkingMemoryEntryPoint.class.getName() );
    }
    
    public ContextManager getContextManager() {
        return context.getContextManager();
    }

    public String getName() {
        return context.getName();
    }

    public Object get(String identifier) {
        return context.get( identifier );
    }

    public void set(String identifier,
                    Object value) {
        context.set( identifier,
                     value );
    }

    public void remove(String name) {
        context.remove( name );
    }    

}
