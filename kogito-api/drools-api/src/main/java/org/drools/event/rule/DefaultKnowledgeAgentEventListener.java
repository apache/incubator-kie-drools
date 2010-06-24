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

public class DefaultKnowledgeAgentEventListener
    implements
    KnowledgeAgentEventListener {

    public void afterChangeSetApplied(AfterChangeSetAppliedEvent event) {
    }

    public void afterChangeSetProcessed(AfterChangeSetProcessedEvent event) {
    }

    public void afterResourceProcessed(AfterResourceProcessedEvent event) {
    }

    public void beforeChangeSetApplied(BeforeChangeSetAppliedEvent event) {
    }

    public void beforeChangeSetProcessed(BeforeChangeSetProcessedEvent event) {
    }

    public void beforeResourceProcessed(BeforeResourceProcessedEvent event) {
    }

    public void knowledgeBaseUpdated(KnowledgeBaseUpdatedEvent event) {
    }

    public void resourceCompilationFailed(ResourceCompilationFailedEvent event) {
    }

}
