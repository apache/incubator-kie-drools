package org.drools.core.command;

import org.drools.core.command.impl.KnowledgeCommandContext;
import org.kie.KnowledgeBase;
import org.kie.builder.KnowledgeBuilder;
import org.kie.command.Context;
import org.kie.command.World;
import org.kie.runtime.ExecutionResults;
import org.kie.runtime.StatefulKnowledgeSession;
import org.kie.runtime.process.WorkItemManager;
import org.kie.runtime.rule.SessionEntryPoint;

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

    public KnowledgeBase getKieBase() {
        return ( KnowledgeBase ) context.get( KnowledgeBase.class.getName() );
    }

    public StatefulKnowledgeSession getKieSession() {
        return ( StatefulKnowledgeSession ) context.get( StatefulKnowledgeSession.class.getName() );
    }

    public WorkItemManager getWorkItemManager() {
        return ( WorkItemManager ) context.get( WorkItemManager.class.getName() );
    }

    public ExecutionResults getExecutionResults() {
        return ( ExecutionResults ) context.get( ExecutionResults.class.getName() );
    }

    public SessionEntryPoint getWorkingMemoryEntryPoint() {
        return ( SessionEntryPoint ) context.get( SessionEntryPoint.class.getName() );
    }
    
    public World getContextManager() {
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
