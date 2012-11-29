package org.kie.runtime;

import org.kie.event.KnowledgeRuntimeEventManager;
import org.kie.runtime.process.StatelessProcessSession;
import org.kie.runtime.rule.StatelessRuleSession;

public interface StatelessKieSession
        extends
        StatelessRuleSession,
        StatelessProcessSession,
        CommandExecutor,
        KnowledgeRuntimeEventManager {

    /**
     * Return the Globals store
     */
    Globals getGlobals();

    /**
     * Sets a global value on the globals store
     * @param identifer the global identifier
     * @param value the value assigned to the global identifier
     */
    void setGlobal(String identifer,
                   Object value);
}
