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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.jbpm.process.instance.ProcessInstanceManager;
import org.jbpm.process.instance.ProcessRuntimeImpl;
import org.jbpm.process.instance.impl.DefaultProcessInstanceManager;
import org.kie.api.KieBase;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.EnvironmentName;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEnvironment;
import org.kie.internal.runtime.manager.SessionFactory;
import org.kie.internal.runtime.manager.SessionNotFoundException;

/**
 * SessionFactory implementation backed with an in-memory store of used sessions. This does not preserve state
 * between server restarts or even <code>RuntimeManager</code> close. For that, the more permanent store 
 * <code>JPASessionFactory</code> should be used.
 *
 * @see JPASessionFactory
 */
public class InMemorySessionFactory implements SessionFactory {

    private AtomicLong processCounter = new AtomicLong(0);
    
    private RuntimeEnvironment environment;
    private KieBase kbase;
    private Map<Long, KieSession> sessions = new ConcurrentHashMap<Long, KieSession>();
    private String owner;
    
    public InMemorySessionFactory(RuntimeEnvironment environment, String owner) {
        this.environment = environment;
        this.kbase = environment.getKieBase();
        this.owner = owner;
    }
    
    @Override
    public KieSession newKieSession() {
        Environment env = environment.getEnvironment();
        env.set(EnvironmentName.DEPLOYMENT_ID, owner);
        KieSession ksession = kbase.newKieSession(environment.getConfiguration(), env);
        this.sessions.put(ksession.getIdentifier(), ksession);
        
        ProcessInstanceManager piManager = ((ProcessRuntimeImpl)((StatefulKnowledgeSessionImpl)ksession).getProcessRuntime()).getProcessInstanceManager();
        if (piManager instanceof DefaultProcessInstanceManager) {
            ((DefaultProcessInstanceManager) piManager).setProcessCounter(processCounter);
        }
        return ksession;
    }

    @Override
    public KieSession findKieSessionById(Long sessionId) {
        if (sessions.containsKey(sessionId)) {
            return sessions.get(sessionId);
        } else {
            throw new SessionNotFoundException("Session with id " + sessionId + " was not found");
        }
    }

    @Override
    public void close() {
        sessions.clear();
    }

    @Override
    public void onDispose(Long sessionId) {
        sessions.remove(sessionId);
    }
    
    protected Map<Long, KieSession> getSessions() {
        return sessions;
    }

}
