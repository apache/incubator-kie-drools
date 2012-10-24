package org.drools;

import org.drools.KnowledgeBase;
import org.drools.builder.KnowledgeBuilderErrors;
import org.drools.runtime.StatefulKnowledgeSession;

/**
 * This class wraps the definition of a named KnowledgeBase defined in a KnowledgeJar
 * allowing to instance it and create new StatefulKnowledgeSession from it
 */
public interface KBaseUnit {

    /**
     * Return the name of the KnowledgeBase wrapped by this KBaseUnit
     * @return
     */
    String getKBaseName();

    /**
     * Lazily create and return the KnowledgeBase wrapped by this KBaseUnit
     * @return
     *     The KnowledgeBase
     */
    KnowledgeBase getKnowledgeBase();

    /**
     * If errors occurred during the build process they are added here
     * @return
     */
    boolean hasErrors();

    /**
     * Return the errors that occurred during the build process of the KnowledgeBase wrapped by this KBaseUnit
     * @return
     */
    KnowledgeBuilderErrors getErrors();

    /**
     * Instance a new StatefulKnowledgeSession identified by the gievn name from the KnowledgeBase wrapped by this KBaseUnit
     * @param kSessionName
     *     The name of the StatefulKnowledgeSession
     * @return
     *     The StatefulKnowledgeSession
     */
    StatefulKnowledgeSession newStatefulKnowledegSession(String ksessionName);
}
