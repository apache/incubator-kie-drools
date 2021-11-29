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

package org.drools.kiesession.rulebase;

import java.util.Collection;

import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.impl.InternalKieContainer;
import org.drools.core.impl.RuleBase;
import org.kie.api.KieBase;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.KieSessionsPool;
import org.kie.api.runtime.StatelessKieSession;

public interface InternalKnowledgeBase extends RuleBase, KieBase {

    KieSession newKieSession(KieSessionConfiguration conf, Environment environment );
    KieSession newKieSession();

    KieSessionsPool newKieSessionsPool(int initialSize);

    Collection<? extends KieSession> getKieSessions();
    Collection<InternalWorkingMemory> getWorkingMemories();

    StatelessKieSession newStatelessKieSession( KieSessionConfiguration conf );

    StatelessKieSession newStatelessKieSession();

    KieSessionsPool getSessionPool();

    void enqueueModification(Runnable modification);
    boolean flushModifications();

    int nextWorkingMemoryCounter();

    void addStatefulSession(InternalWorkingMemory wm);

    KieSession newKieSession(KieSessionConfiguration conf, Environment environment, boolean fromPool);

    void setKieContainer( InternalKieContainer kieContainer );

    void disposeStatefulSession(InternalWorkingMemory statefulSession);

    InternalKieContainer getKieContainer();

    void initMBeans();
}
