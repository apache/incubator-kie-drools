/*
 * Copyright 2013 JBoss Inc
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
package org.jbpm.runtime.manager.impl.factory;

import org.drools.core.command.impl.CommandBasedStatefulKnowledgeSession;
import org.drools.persistence.SingleSessionCommandService;
import org.drools.persistence.jpa.OptimisticLockRetryInterceptor;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEnvironment;
import org.kie.internal.persistence.jpa.JPAKnowledgeService;
import org.kie.internal.runtime.manager.SessionFactory;

/**
 * SessionFactory implementation that is backed by data base for storing <code>KieSession</code> data.
 *
 */
public class JPASessionFactory implements SessionFactory {

    private RuntimeEnvironment environment;
    
    public JPASessionFactory(RuntimeEnvironment environment) {
        this.environment = environment;
    }
    
    @Override
    public KieSession newKieSession() {

        KieSession ksession = JPAKnowledgeService.newStatefulKnowledgeSession(
                environment.getKieBase(), environment.getConfiguration(), environment.getEnvironment());
        addInterceptors(ksession);
        return ksession;
    }

    @Override
    public KieSession findKieSessionById(Integer sessionId) {
        if (sessionId == null) {
            return null;
        }
        KieSession ksession = JPAKnowledgeService.loadStatefulKnowledgeSession(sessionId,
                environment.getKieBase(), environment.getConfiguration(), environment.getEnvironment());
        addInterceptors(ksession);
        return ksession;
    }

    @Override
    public void close() {
        
    }
    
    protected void addInterceptors(KieSession ksession) {
        
        SingleSessionCommandService sscs = (SingleSessionCommandService)
                ((CommandBasedStatefulKnowledgeSession) ksession).getCommandService();
        sscs.addInterceptor(new OptimisticLockRetryInterceptor());
    }
    
}
