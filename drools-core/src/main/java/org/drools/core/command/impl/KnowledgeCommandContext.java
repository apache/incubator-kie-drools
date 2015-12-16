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

package org.drools.core.command.impl;

import org.kie.api.KieBase;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.command.Context;
import org.kie.api.runtime.ExecutionResults;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.WorkItemManager;
import org.kie.api.runtime.rule.EntryPoint;

public interface KnowledgeCommandContext extends Context {
    
    public KnowledgeBuilder getKnowledgeBuilder();
    
    public void setKnowledgeBuilder(KnowledgeBuilder kbuilder);

    public KieBase getKieBase();

    public KieSession getKieSession();

    public WorkItemManager getWorkItemManager();

    public ExecutionResults getExecutionResults();

    public EntryPoint getWorkingMemoryEntryPoint();

}
