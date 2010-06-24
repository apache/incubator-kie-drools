package org.drools.event.rule;

import org.drools.event.knowledgeagent.AfterChangeSetAppliedEvent;
import org.drools.event.knowledgeagent.AfterChangeSetProcessedEvent;
import org.drools.event.knowledgeagent.AfterResourceProcessedEvent;
import org.drools.event.knowledgeagent.BeforeChangeSetAppliedEvent;
import org.drools.event.knowledgeagent.BeforeChangeSetProcessedEvent;
import org.drools.event.knowledgeagent.BeforeResourceProcessedEvent;
import org.drools.event.knowledgeagent.KnowledgeAgentEventListener;
import org.drools.event.knowledgeagent.KnowledgeBaseUpdatedEvent;
import org.drools.event.knowledgeagent.ResourceCompilationFailedEvent;

public class DebugKnowledgeAgentEventListener
    implements
    KnowledgeAgentEventListener {

    public void afterChangeSetApplied(AfterChangeSetAppliedEvent event) {
        System.err.print(event);
    }

    public void afterChangeSetProcessed(AfterChangeSetProcessedEvent event) {
        System.err.print(event);
    }

    public void afterResourceProcessed(AfterResourceProcessedEvent event) {
        System.err.print(event);
    }

    public void beforeChangeSetApplied(BeforeChangeSetAppliedEvent event) {
        System.err.print(event);
    }

    public void beforeChangeSetProcessed(BeforeChangeSetProcessedEvent event) {
        System.err.print(event);
    }

    public void beforeResourceProcessed(BeforeResourceProcessedEvent event) {
        System.err.print(event);
    }

    public void knowledgeBaseUpdated(KnowledgeBaseUpdatedEvent event) {
        System.err.print(event);
    }

    public void resourceCompilationFailed(ResourceCompilationFailedEvent event) {
        System.err.print(event);
    }

}
