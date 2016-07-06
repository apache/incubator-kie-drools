/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.core.command;

import org.drools.core.command.impl.KnowledgeCommandContext;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.command.Context;
import org.kie.internal.command.ContextManager;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.kie.api.runtime.ExecutionResults;
import org.kie.api.runtime.process.WorkItemManager;
import org.kie.api.runtime.rule.EntryPoint;

public class ResolvingKnowledgeCommandContext implements KnowledgeCommandContext {
    
    private Context                  context;

    public ResolvingKnowledgeCommandContext(Context context) {
        super();
        this.context = context;
    }
    
    public KnowledgeBuilder getKnowledgeBuilder() {
        return ( KnowledgeBuilder ) context.get( KnowledgeBuilder.class.getName() );
    }

    public void setKnowledgeBuilder(KnowledgeBuilder kbuilder) {
        context.set( KnowledgeBuilder.class.getName(), kbuilder );
    }

    public KnowledgeBase getKieBase() {
        return ( KnowledgeBase ) context.get( KnowledgeBase.class.getName() );
    }

    public StatefulKnowledgeSession getKieSession() {
        return (StatefulKnowledgeSession) context.get( StatefulKnowledgeSession.class.getName() );
    }

    public WorkItemManager getWorkItemManager() {
        return ( WorkItemManager ) context.get( WorkItemManager.class.getName() );
    }

    public ExecutionResults getExecutionResults() {
        return ( ExecutionResults ) context.get( ExecutionResults.class.getName() );
    }

    public EntryPoint getWorkingMemoryEntryPoint() {
        return ( EntryPoint ) context.get( EntryPoint.class.getName() );
    }
    
    public ContextManager getContextManager() {
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
