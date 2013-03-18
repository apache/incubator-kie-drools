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

package org.drools.core.command.impl;

import org.kie.KieBase;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.command.Context;
import org.kie.internal.command.World;
import org.kie.runtime.ExecutionResults;
import org.kie.runtime.KieSession;
import org.kie.runtime.process.WorkItemManager;
import org.kie.runtime.rule.SessionEntryPoint;

public class FixedKnowledgeCommandContext
        implements
        KnowledgeCommandContext {

    private Context                  context;
    private KnowledgeBuilder         kbuilder;
    private KieBase                  kbase;
    private KieSession               kSession;
    private SessionEntryPoint        workingMemoryEntryPoint;
    private ExecutionResults         kresults;

    public FixedKnowledgeCommandContext(Context context,
                                        KnowledgeBuilder kbuilder,
                                        KieBase kbase,
                                        KieSession statefulKsession,
                                        ExecutionResults kresults) {
        this.context = context;
        this.kbuilder = kbuilder;
        this.kbase = kbase;
        this.kSession = statefulKsession;
        this.kresults = kresults;
    }

    public FixedKnowledgeCommandContext(Context context,
                                        KnowledgeBuilder kbuilder,
                                        KieBase kbase,
                                        KieSession statefulKsession,
                                        SessionEntryPoint workingMemoryEntryPoint,
                                        ExecutionResults kresults) {
        this( context,
              kbuilder,
              kbase,
              statefulKsession,
              kresults );
        this.workingMemoryEntryPoint = workingMemoryEntryPoint;
    }

    public KnowledgeBuilder getKnowledgeBuilder() {
        return kbuilder;
    }

    public KieBase getKieBase() {
        return this.kbase;
    }

    public KieSession getKieSession() {
        return kSession;
    }

    public WorkItemManager getWorkItemManager() {
        return kSession.getWorkItemManager();
    }

    public ExecutionResults getExecutionResults() {
        return this.kresults;
    }

    public SessionEntryPoint getWorkingMemoryEntryPoint() {
        return workingMemoryEntryPoint;
    }

    public void setWorkingMemoryEntryPoint(SessionEntryPoint workingMemoryEntryPoint) {
        this.workingMemoryEntryPoint = workingMemoryEntryPoint;
    }

    public void setKnowledgeBuilder(KnowledgeBuilder kbuilder) {
        this.kbuilder = kbuilder;
    }

    public void setKbase(KieBase kbase) {
        this.kbase = kbase;
    }

    public World getContextManager() {
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
