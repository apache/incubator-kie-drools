/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.runtime.manager.impl.factory;

import org.drools.core.command.impl.CommandBasedStatefulKnowledgeSession;
import org.drools.persistence.PersistableRunner;
import org.drools.persistence.jpa.OptimisticLockRetryInterceptor;
import org.drools.persistence.jta.TransactionLockInterceptor;
import org.jbpm.runtime.manager.impl.error.ExecutionErrorHandlerInterceptor;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.EnvironmentName;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEnvironment;
import org.kie.internal.persistence.jpa.JPAKnowledgeService;
import org.kie.internal.runtime.manager.SessionFactory;

/**
 * SessionFactory implementation that is backed by a database for storing <code>KieSession</code> data.
 *
 */
public class JPASessionFactory implements SessionFactory {

    private RuntimeEnvironment environment;
    private String owner;
    
    public JPASessionFactory(RuntimeEnvironment environment, String owner) {
        this.environment = environment;
        this.owner = owner;
    }
    
    @Override
    public KieSession newKieSession() {
        Environment env = environment.getEnvironment();
        env.set(EnvironmentName.DEPLOYMENT_ID, owner);
        KieSession ksession = JPAKnowledgeService.newStatefulKnowledgeSession(
                environment.getKieBase(), environment.getConfiguration(), env);
        addInterceptors(ksession);
        return ksession;
    }

    @Override
    public KieSession findKieSessionById(Long sessionId) {
        if (sessionId == null) {
            return null;
        }
        Environment env = environment.getEnvironment();
        env.set(EnvironmentName.DEPLOYMENT_ID, owner);
        KieSession ksession = JPAKnowledgeService.loadStatefulKnowledgeSession(sessionId,
                environment.getKieBase(), environment.getConfiguration(), env);
        addInterceptors(ksession);
        return ksession;
    }

    @Override
    public void close() {
        
    }
    
    protected void addInterceptors(KieSession ksession) {

        PersistableRunner runner = (PersistableRunner)
                ((CommandBasedStatefulKnowledgeSession) ksession).getRunner();        
        runner.addInterceptor(new OptimisticLockRetryInterceptor());
        // even though it's added always TransactionLockInterceptor is by default disabled so won't do anything
        runner.addInterceptor(new TransactionLockInterceptor(ksession.getEnvironment()));
        runner.addInterceptor(new ExecutionErrorHandlerInterceptor(ksession.getEnvironment()));
    }

    @Override
    public void onDispose(Long sessionId) {
        // no op
    }
    
}
