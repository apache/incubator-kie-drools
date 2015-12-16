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

package org.kie.internal.event.rule;

import java.io.PrintStream;

import org.kie.internal.event.knowledgeagent.AfterChangeSetAppliedEvent;
import org.kie.internal.event.knowledgeagent.AfterChangeSetProcessedEvent;
import org.kie.internal.event.knowledgeagent.AfterResourceProcessedEvent;
import org.kie.internal.event.knowledgeagent.BeforeChangeSetAppliedEvent;
import org.kie.internal.event.knowledgeagent.BeforeChangeSetProcessedEvent;
import org.kie.internal.event.knowledgeagent.BeforeResourceProcessedEvent;
import org.kie.internal.event.knowledgeagent.KnowledgeAgentEventListener;
import org.kie.internal.event.knowledgeagent.KnowledgeBaseUpdatedEvent;
import org.kie.internal.event.knowledgeagent.ResourceCompilationFailedEvent;

public class DebugKnowledgeAgentEventListener
    implements
    KnowledgeAgentEventListener {
    
    private PrintStream stream;
    
    public DebugKnowledgeAgentEventListener() {
        this.stream =  System.err;
    }
    
    public DebugKnowledgeAgentEventListener(PrintStream stream) {
        this.stream = stream;
    }    
    
    public void afterChangeSetApplied(AfterChangeSetAppliedEvent event) {
        stream.println( event );
    }

    public void afterChangeSetProcessed(AfterChangeSetProcessedEvent event) {
        stream.println( event );
    }

    public void afterResourceProcessed(AfterResourceProcessedEvent event) {
        stream.println( event );
    }

    public void beforeChangeSetApplied(BeforeChangeSetAppliedEvent event) {
        stream.println( event );
    }

    public void beforeChangeSetProcessed(BeforeChangeSetProcessedEvent event) {
        stream.println( event );
    }

    public void beforeResourceProcessed(BeforeResourceProcessedEvent event) {
        stream.println( event );
    }

    public void knowledgeBaseUpdated(KnowledgeBaseUpdatedEvent event) {
        stream.println( event );
    }

    public void resourceCompilationFailed(ResourceCompilationFailedEvent event) {
        stream.println( event );
    }

}
