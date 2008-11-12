package org.drools.runtime;

import org.drools.KnowledgeBase;
import org.drools.runtime.process.StatefulProcessSession;
import org.drools.runtime.rule.StatefulRuleSession;

public interface StatefulKnowledgeSession extends StatefulRuleSession, StatefulProcessSession, KnowledgeRuntime {

    void setGlobal(String identifier,
                   Object object);
    
    /**
     * Delegate used to resolve any global names not found in the global map.
     * @param globalResolver
     */
    void setGlobalResolver(GlobalResolver globalResolver);    

    KnowledgeBase getKnowledgeBase();

    void setFocus(String string);

}
