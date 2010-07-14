package org.drools.persistence.jpa;

import org.drools.KnowledgeBase;
import org.drools.runtime.Environment;
import org.drools.runtime.KnowledgeSessionConfiguration;
import org.drools.runtime.StatefulKnowledgeSession;

public interface KnowledgeStoreService {

    StatefulKnowledgeSession newStatefulKnowledgeSession(KnowledgeBase kbase,
                                                         KnowledgeSessionConfiguration configuration,
                                                         Environment environment);

    StatefulKnowledgeSession loadStatefulKnowledgeSession(int id,
                                                          KnowledgeBase kbase,
                                                          KnowledgeSessionConfiguration configuration,
                                                          Environment environment);

}
