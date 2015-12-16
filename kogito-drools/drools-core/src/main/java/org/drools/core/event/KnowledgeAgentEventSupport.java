/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.core.event;


import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kie.internal.ChangeSet;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.agent.KnowledgeAgent.ResourceStatus;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.definition.KnowledgeDefinition;
import org.kie.internal.event.knowledgeagent.AfterChangeSetAppliedEvent;
import org.kie.internal.event.knowledgeagent.AfterChangeSetProcessedEvent;
import org.kie.internal.event.knowledgeagent.AfterResourceProcessedEvent;
import org.kie.internal.event.knowledgeagent.BeforeChangeSetAppliedEvent;
import org.kie.internal.event.knowledgeagent.BeforeChangeSetProcessedEvent;
import org.kie.internal.event.knowledgeagent.BeforeResourceProcessedEvent;
import org.kie.internal.event.knowledgeagent.KnowledgeAgentEventListener;
import org.kie.internal.event.knowledgeagent.KnowledgeBaseUpdatedEvent;
import org.kie.internal.event.knowledgeagent.ResourceCompilationFailedEvent;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;

public class KnowledgeAgentEventSupport extends AbstractEventSupport<KnowledgeAgentEventListener> {

    public KnowledgeAgentEventSupport() {
    }

    public void fireBeforeChangeSetApplied(ChangeSet changeSet) {
        final Iterator<KnowledgeAgentEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            final BeforeChangeSetAppliedEvent event = new BeforeChangeSetAppliedEvent(changeSet);
            do{
                iter.next().beforeChangeSetApplied(event);
            }  while (iter.hasNext());
        }
    }

    public void fireAfterChangeSetApplied(ChangeSet changeSet) {
        final Iterator<KnowledgeAgentEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            final AfterChangeSetAppliedEvent event = new AfterChangeSetAppliedEvent(changeSet);
            do{
                iter.next().afterChangeSetApplied(event);
            }  while (iter.hasNext());
        }
    }

    public void fireBeforeChangeSetProcessed(ChangeSet changeSet) {
        final Iterator<KnowledgeAgentEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            do{
                final BeforeChangeSetProcessedEvent event = new BeforeChangeSetProcessedEvent(changeSet);
                iter.next().beforeChangeSetProcessed(event);
            }  while (iter.hasNext());
        }
    }

    public void fireAfterChangeSetProcessed(ChangeSet changeSet, List<Resource> addedResources, Map<Resource, Set<KnowledgeDefinition>> modifiedResourceMappings, Map<Resource, Set<KnowledgeDefinition>> removedResourceMappings) {
        final Iterator<KnowledgeAgentEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            do{
                final AfterChangeSetProcessedEvent event = new AfterChangeSetProcessedEvent(changeSet, addedResources,  modifiedResourceMappings, removedResourceMappings);
                iter.next().afterChangeSetProcessed(event);
            }  while (iter.hasNext());
        }
    }

    public void fireBeforeResourceProcessed(ChangeSet changeSet, Resource resource, ResourceType type, ResourceStatus status) {
        final Iterator<KnowledgeAgentEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            do{
                final BeforeResourceProcessedEvent event = new BeforeResourceProcessedEvent(changeSet, resource, type, status);
                iter.next().beforeResourceProcessed(event);
            }  while (iter.hasNext());
        }
    }

    public void fireAfterResourceProcessed(ChangeSet changeSet, Resource resource, ResourceType type, ResourceStatus status) {
        final Iterator<KnowledgeAgentEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            do{
                final AfterResourceProcessedEvent event = new AfterResourceProcessedEvent(changeSet, resource, type, status);
                iter.next().afterResourceProcessed(event);
            }  while (iter.hasNext());
        }
    }
    
    public void fireKnowledgeBaseUpdated(KnowledgeBase kbase) {
        final Iterator<KnowledgeAgentEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            do{
                final KnowledgeBaseUpdatedEvent event = new KnowledgeBaseUpdatedEvent(kbase);
                iter.next().knowledgeBaseUpdated(event);
            }  while (iter.hasNext());
        }
    }

    public void fireResourceCompilationFailed(KnowledgeBuilder kbuilder, Resource resource, ResourceType type) {
        final Iterator<KnowledgeAgentEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            do{
                final ResourceCompilationFailedEvent event = new ResourceCompilationFailedEvent(kbuilder, resource, type);
                iter.next().resourceCompilationFailed(event);
            }  while (iter.hasNext());
        }
    }


    public void reset() {
        this.clear();
    }

}
