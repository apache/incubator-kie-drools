package org.drools.core.command.impl;

import org.kie.api.KieBase;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.command.Context;
import org.kie.api.runtime.ExecutionResults;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.WorkItemManager;
import org.kie.api.runtime.rule.EntryPoint;

public interface KnowledgeCommandContext extends Context {
    
    public KnowledgeBuilder getKnowledgeBuilder();
    
    public void setKnowledgeBuilder(KnowledgeBuilder kbuilder);

    public KieBase getKieBase();

    public KieSession getKieSession();

    public WorkItemManager getWorkItemManager();

    public ExecutionResults getExecutionResults();

    public EntryPoint getWorkingMemoryEntryPoint();

}
