package org.kie.runtime;

import org.kie.runtime.process.StatefulProcessSession;
import org.kie.runtime.rule.StatefulRuleSession;

public interface KieSession
        extends
        StatefulRuleSession,
        StatefulProcessSession,
        CommandExecutor,
        KieRuntime {

    int getId();

    /**
     * Releases all the current session resources, setting up the session for garbage collection.
     * This method <b>must</b> always be called after finishing using the session, or the engine
     * will not free the memory used by the session.
     */
    void dispose();
}
