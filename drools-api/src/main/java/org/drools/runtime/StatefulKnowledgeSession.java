package org.drools.runtime;

import org.drools.KnowledgeBase;
import org.drools.runtime.process.StatefulProcessSession;
import org.drools.runtime.rule.StatefulRuleSession;

public interface StatefulKnowledgeSession extends StatefulRuleSession, StatefulProcessSession, KnowledgeRuntime {

    void setGlobal(String identifier,
                   Object object);

    KnowledgeBase getKnowledgeBase();

    void setFocus(String string);

}
