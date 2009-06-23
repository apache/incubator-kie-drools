package org.drools.runtime;

import org.drools.KnowledgeBase;
import org.drools.event.KnowledgeRuntimeEventManager;
import org.drools.runtime.process.ProcessRuntime;
import org.drools.runtime.rule.WorkingMemory;

public interface KnowledgeRuntime
    extends
    WorkingMemory,
    ProcessRuntime,
    KnowledgeRuntimeEventManager {

    /**
     * Sets a global value on the internal collection
     * @param identifer the global identifier
     * @param value the value assigned to the global identifier
     */
    void setGlobal(String identifier,
                   Object object);

    Object getGlobal(String identifier);

    Globals getGlobals();

    Environment getEnvironment();

    /**
     * Returns the KnowledgeBase reference from which this stateful session was created.
     * 
     * @return
     */
    KnowledgeBase getKnowledgeBase();

    void registerExitPoint(String name,
                           ExitPoint exitPoint);

    void unregisterExitPoint(String name);

}
