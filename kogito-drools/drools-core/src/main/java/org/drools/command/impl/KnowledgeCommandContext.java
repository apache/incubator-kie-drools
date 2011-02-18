/**
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

package org.drools.command.impl;

import org.drools.KnowledgeBase;
import org.drools.builder.KnowledgeBuilder;
import org.drools.command.Context;
import org.drools.command.ContextManager;
import org.drools.runtime.ExecutionResults;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.WorkItemManager;
import org.drools.runtime.rule.WorkingMemoryEntryPoint;

public class KnowledgeCommandContext
    implements
    Context {
    private Context                  context;
    private KnowledgeBuilder         kbuilder;
    private KnowledgeBase            kbase;
    private StatefulKnowledgeSession statefulKsession;
    private WorkingMemoryEntryPoint  workingMemoryEntryPoint;
    private ExecutionResults         kresults;

    public KnowledgeCommandContext(Context context,
                                   KnowledgeBuilder kbuilder,
                                   KnowledgeBase kbase,
                                   StatefulKnowledgeSession statefulKsession,
                                   ExecutionResults         kresults) {
        this.context = context;
        this.kbuilder = kbuilder;
        this.kbase = kbase;
        this.statefulKsession = statefulKsession;
        this.kresults = kresults;
    }
    public KnowledgeCommandContext(Context context,
                                   KnowledgeBuilder kbuilder,
                                   KnowledgeBase kbase,
                                   StatefulKnowledgeSession statefulKsession,
                                   WorkingMemoryEntryPoint  workingMemoryEntryPoint,
                                   ExecutionResults         kresults) {
        this(context, kbuilder, kbase, statefulKsession, kresults);
        this.workingMemoryEntryPoint = workingMemoryEntryPoint;
    }

    public KnowledgeBuilder getKnowledgeBuilder() {
        return kbuilder;
    }

    public KnowledgeBase getKnowledgeBase() {
        return this.kbase;
    }       

    public StatefulKnowledgeSession getStatefulKnowledgesession() {
        return statefulKsession;
    }
    
    public WorkItemManager getWorkItemManager() {
    	return statefulKsession.getWorkItemManager();
    }
    
    public ExecutionResults getExecutionResults() {
        return this.kresults;
    }

    public WorkingMemoryEntryPoint getWorkingMemoryEntryPoint() {
        return workingMemoryEntryPoint;
    }

    public void setWorkingMemoryEntryPoint(WorkingMemoryEntryPoint workingMemoryEntryPoint) {
        this.workingMemoryEntryPoint = workingMemoryEntryPoint;
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
        context.remove(name);
    }

}
