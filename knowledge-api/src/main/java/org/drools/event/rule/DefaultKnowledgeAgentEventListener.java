/*
 * Copyright 2010 JBoss Inc
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

package org.drools.core.event.rule;

import org.drools.core.event.knowledgeagent.AfterChangeSetAppliedEvent;
import org.drools.core.event.knowledgeagent.AfterChangeSetProcessedEvent;
import org.drools.core.event.knowledgeagent.AfterResourceProcessedEvent;
import org.drools.core.event.knowledgeagent.BeforeChangeSetAppliedEvent;
import org.drools.core.event.knowledgeagent.BeforeChangeSetProcessedEvent;
import org.drools.core.event.knowledgeagent.BeforeResourceProcessedEvent;
import org.drools.core.event.knowledgeagent.KnowledgeAgentEventListener;
import org.drools.core.event.knowledgeagent.KnowledgeBaseUpdatedEvent;
import org.drools.core.event.knowledgeagent.ResourceCompilationFailedEvent;

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
