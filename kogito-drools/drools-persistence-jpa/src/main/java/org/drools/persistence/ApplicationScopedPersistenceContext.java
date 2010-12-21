package org.drools.persistence;

import org.drools.runtime.KnowledgeSessionConfiguration;
import org.drools.runtime.StatefulKnowledgeSession;

public interface ApplicationScopedPersistenceContext {

    StatefulKnowledgeSession loadStatefulKnowledgeSession(int sessionId,
            KnowledgeSessionConfiguration kconf);

    void save(StatefulKnowledgeSession internalKnowledgeSession);

    void update(StatefulKnowledgeSession internalKnowledgeSession);

}
