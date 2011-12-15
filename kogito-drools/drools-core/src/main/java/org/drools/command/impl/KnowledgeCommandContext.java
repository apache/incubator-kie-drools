package org.drools.command.impl;

import org.drools.KnowledgeBase;
import org.drools.builder.KnowledgeBuilder;
import org.drools.command.Context;
import org.drools.runtime.ExecutionResults;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.WorkItemManager;
import org.drools.runtime.rule.WorkingMemoryEntryPoint;

public interface KnowledgeCommandContext extends Context{

    public KnowledgeBuilder getKnowledgeBuilder();

    public KnowledgeBase getKnowledgeBase();

    public StatefulKnowledgeSession getStatefulKnowledgesession();

    public WorkItemManager getWorkItemManager();

    public ExecutionResults getExecutionResults();

    public WorkingMemoryEntryPoint getWorkingMemoryEntryPoint();

}