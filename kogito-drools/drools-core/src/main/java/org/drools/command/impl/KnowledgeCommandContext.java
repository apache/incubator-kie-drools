package org.drools.command.impl;

import org.kie.KBaseUnit;
import org.kie.KnowledgeBase;
import org.kie.builder.KnowledgeBuilder;
import org.kie.builder.KnowledgeContainer;
import org.kie.command.Context;
import org.kie.runtime.ExecutionResults;
import org.kie.runtime.StatefulKnowledgeSession;
import org.kie.runtime.process.WorkItemManager;
import org.kie.runtime.rule.WorkingMemoryEntryPoint;

public interface KnowledgeCommandContext extends Context {
    
    public KnowledgeContainer getKnowledgeContainer();

    public KnowledgeBuilder getKnowledgeBuilder();
    
    public void setKnowledgeBuilder(KnowledgeBuilder kbuilder);

    public KBaseUnit getKBaseUnit();
    
    public void setKBaseUnit( KBaseUnit unit );

    public KnowledgeBase getKnowledgeBase();

    public StatefulKnowledgeSession getStatefulKnowledgesession();

    public WorkItemManager getWorkItemManager();

    public ExecutionResults getExecutionResults();

    public WorkingMemoryEntryPoint getWorkingMemoryEntryPoint();

}