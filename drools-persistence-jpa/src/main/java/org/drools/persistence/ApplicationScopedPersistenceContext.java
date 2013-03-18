package org.drools.persistence;

import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.kie.runtime.Environment;
import org.kie.runtime.KieSessionConfiguration;

public interface ApplicationScopedPersistenceContext {

    StatefulKnowledgeSession loadStatefulKnowledgeSession(long sessionId,
            KieSessionConfiguration kconf,
            Environment env);

    void save(StatefulKnowledgeSession internalKnowledgeSession);

    void update(StatefulKnowledgeSession internalKnowledgeSession);

    void setLastModificationDate(long sessionId);
}
