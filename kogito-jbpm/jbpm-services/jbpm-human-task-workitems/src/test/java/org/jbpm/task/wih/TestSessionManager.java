package org.jbpm.task.wih;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import org.jbpm.shared.services.api.SessionManager;
import org.jbpm.task.test.TestStatefulKnowledgeSession;
import org.kie.api.runtime.KieSession;

@ApplicationScoped
public class TestSessionManager implements SessionManager {
    
    private KieSession ksession;
    
    @PostConstruct
    public void configure() {
        this.ksession = new TestStatefulKnowledgeSession();
    }

    @Override
    public KieSession getKsessionById(int sessionId) {
        return ksession;
    }

    @Override
    public int getSessionForProcessInstanceId(Long processInstanceId) {
        return ksession.getId();
    }

}
