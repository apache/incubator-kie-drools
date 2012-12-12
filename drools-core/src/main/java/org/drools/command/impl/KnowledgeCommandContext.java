package org.drools.command.impl;

import org.kie.KnowledgeBase;
import org.kie.builder.KnowledgeBuilder;
import org.kie.command.Context;
import org.kie.runtime.ExecutionResults;
import org.kie.runtime.StatefulKnowledgeSession;
import org.kie.runtime.process.WorkItemManager;
import org.kie.runtime.rule.SessionEntryPoint;

public interface KnowledgeCommandContext extends Context {
    
    public KnowledgeBuilder getKnowledgeBuilder();
    
    public void setKnowledgeBuilder(KnowledgeBuilder kbuilder);

    public KnowledgeBase getKnowledgeBase();

    public StatefulKnowledgeSession getStatefulKnowledgesession();

    public WorkItemManager getWorkItemManager();

    public ExecutionResults getExecutionResults();

    public SessionEntryPoint getWorkingMemoryEntryPoint();

}