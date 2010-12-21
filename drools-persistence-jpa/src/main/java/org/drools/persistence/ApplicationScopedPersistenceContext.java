package org.drools.persistence;

import org.drools.runtime.Environment;
import org.drools.runtime.KnowledgeSessionConfiguration;
import org.drools.runtime.StatefulKnowledgeSession;

public interface ApplicationScopedPersistenceContext {

    StatefulKnowledgeSession loadStatefulKnowledgeSession(long sessionId,
            KnowledgeSessionConfiguration kconf,
            Environment env);

    void save(StatefulKnowledgeSession internalKnowledgeSession);

    void update(StatefulKnowledgeSession internalKnowledgeSession);

    void setLastModificationDate(long sessionId);
}
