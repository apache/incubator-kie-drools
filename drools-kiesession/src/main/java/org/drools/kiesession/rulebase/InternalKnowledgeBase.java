package org.drools.kiesession.rulebase;

import java.util.Collection;

import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.impl.InternalKieContainer;
import org.drools.core.impl.InternalRuleBase;
import org.kie.api.KieBase;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.KieSessionsPool;
import org.kie.api.runtime.StatelessKieSession;

public interface InternalKnowledgeBase extends InternalRuleBase, KieBase {

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
