package org.drools.persistence;

import org.kie.runtime.Environment;
import org.kie.runtime.KnowledgeSessionConfiguration;
import org.kie.runtime.StatefulKnowledgeSession;

public interface ApplicationScopedPersistenceContext {

    StatefulKnowledgeSession loadStatefulKnowledgeSession(long sessionId,
            KnowledgeSessionConfiguration kconf,
            Environment env);

    void save(StatefulKnowledgeSession internalKnowledgeSession);

    void update(StatefulKnowledgeSession internalKnowledgeSession);

    void setLastModificationDate(long sessionId);
}
