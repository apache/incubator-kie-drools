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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.kie.api.KieBase;
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

    private RuntimeEnvironment environment;
    private KieBase kbase;
    // TODO all sessions stored here should be proxied so it can be removed on dispose/destroy
    private Map<Long, KieSession> sessions = new ConcurrentHashMap<Long, KieSession>();
    
    public InMemorySessionFactory(RuntimeEnvironment environment) {
        this.environment = environment;
        this.kbase = environment.getKieBase();
    }
    
    @Override
    public KieSession newKieSession() {
        KieSession ksession = kbase.newKieSession(environment.getConfiguration(), environment.getEnvironment());
        this.sessions.put(ksession.getIdentifier(), ksession);
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

}
