package org.drools.command;

import org.drools.command.impl.KnowledgeCommandContext;
import org.kie.KBaseUnit;
import org.kie.KnowledgeBase;
import org.kie.builder.KnowledgeBuilder;
import org.kie.builder.KnowledgeContainer;
import org.kie.builder.KnowledgeContainerFactory;
import org.kie.command.Context;
import org.kie.command.World;
import org.kie.runtime.ExecutionResults;
import org.kie.runtime.StatefulKnowledgeSession;
import org.kie.runtime.process.WorkItemManager;
import org.kie.runtime.rule.WorkingMemoryEntryPoint;

public class ResolvingKnowledgeCommandContext implements KnowledgeCommandContext {
    
    private Context                  context;

    public ResolvingKnowledgeCommandContext(Context context) {
        super();
        this.context = context;
    }
    
    public KnowledgeContainer getKnowledgeContainer() {
        KnowledgeContainer kcontainer = (KnowledgeContainer) context.get( KnowledgeContainer.class.getName() );
        if( kcontainer == null ) {
            kcontainer = KnowledgeContainerFactory.newKnowledgeContainer();
            context.set( KnowledgeContainer.class.getName(), kcontainer );
        }
        return kcontainer;
    }

    public KnowledgeBuilder getKnowledgeBuilder() {
        return ( KnowledgeBuilder ) context.get( KnowledgeBuilder.class.getName() );
    }

    public void setKnowledgeBuilder(KnowledgeBuilder kbuilder) {
        context.set( KnowledgeBuilder.class.getName(), kbuilder );
    }

    public KBaseUnit getKBaseUnit() {
        return ( KBaseUnit ) context.get( KBaseUnit.class.getName() );
    }
    
    public void setKBaseUnit( KBaseUnit unit ) {
        context.set( KBaseUnit.class.getName(), unit );
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
