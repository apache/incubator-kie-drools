package org.jbpm.runtime.manager.impl.factory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.kie.KieBase;
import org.kie.runtime.KieSession;
import org.kie.runtime.manager.RuntimeEnvironment;
import org.kie.runtime.manager.SessionFactory;
import org.kie.runtime.manager.SessionNotFoundException;

public class InMemorySessionFactory implements SessionFactory {

    private RuntimeEnvironment environment;
    private KieBase kbase;
    // TODO all sessions stored here should be proxied so it can be removed on dispose/destroy
    private Map<Integer, KieSession> sessions = new ConcurrentHashMap<Integer, KieSession>();
    
    public InMemorySessionFactory(RuntimeEnvironment environment) {
        this.environment = environment;
        this.kbase = environment.getKieBase();
    }
    
    @Override
    public KieSession newKieSession() {
        KieSession ksession = kbase.newKieSession(environment.getConfiguration(), environment.getEnvironment());
        this.sessions.put(ksession.getId(), ksession);
        return ksession;
    }

    @Override
    public KieSession findKieSessionById(Integer sessionId) {
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
