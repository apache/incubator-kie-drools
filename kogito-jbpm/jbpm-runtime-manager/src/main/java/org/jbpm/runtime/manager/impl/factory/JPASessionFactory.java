package org.jbpm.runtime.manager.impl.factory;

import org.kie.api.runtime.KieSession;
import org.kie.internal.persistence.jpa.JPAKnowledgeService;
import org.kie.internal.runtime.manager.RuntimeEnvironment;
import org.kie.internal.runtime.manager.SessionFactory;

public class JPASessionFactory implements SessionFactory {

    private RuntimeEnvironment environment;
    
    public JPASessionFactory(RuntimeEnvironment environment) {
        this.environment = environment;
    }
    
    @Override
    public KieSession newKieSession() {

        KieSession ksession = JPAKnowledgeService.newStatefulKnowledgeSession(
                environment.getKieBase(), environment.getConfiguration(), environment.getEnvironment());
                
        return ksession;
    }

    @Override
    public KieSession findKieSessionById(Integer sessionId) {
        if (sessionId == null) {
            return null;
        }
        KieSession ksession = JPAKnowledgeService.loadStatefulKnowledgeSession(sessionId,
                environment.getKieBase(), environment.getConfiguration(), environment.getEnvironment());
        
        return ksession;
    }

    @Override
    public void close() {
        
    }
    

    
}
