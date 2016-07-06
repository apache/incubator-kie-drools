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

package org.drools.core.command.impl;

import org.kie.api.KieBase;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.command.Context;
import org.kie.internal.command.ContextManager;
import org.kie.api.runtime.ExecutionResults;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.WorkItemManager;
import org.kie.api.runtime.rule.EntryPoint;

public class FixedKnowledgeCommandContext
        implements
        KnowledgeCommandContext {

    private Context                  context;
    private KnowledgeBuilder         kbuilder;
    private KieBase                  kbase;
    private KieSession               kSession;
    private EntryPoint        workingMemoryEntryPoint;
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
                                        EntryPoint workingMemoryEntryPoint,
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

    public EntryPoint getWorkingMemoryEntryPoint() {
        return workingMemoryEntryPoint;
    }

    public void setWorkingMemoryEntryPoint(EntryPoint workingMemoryEntryPoint) {
        this.workingMemoryEntryPoint = workingMemoryEntryPoint;
    }

    public void setKnowledgeBuilder(KnowledgeBuilder kbuilder) {
        this.kbuilder = kbuilder;
    }

    public void setKbase(KieBase kbase) {
        this.kbase = kbase;
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
