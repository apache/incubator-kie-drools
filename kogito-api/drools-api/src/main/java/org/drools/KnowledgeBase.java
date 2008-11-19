package org.drools;

import java.util.Collection;

import org.drools.definition.KnowledgePackage;
import org.drools.event.knowledgebase.KnowledgeBaseEventManager;
import org.drools.runtime.KnowledgeSessionConfiguration;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.StatelessKnowledgeSession;

/**
 * <p>
 * The KnowlegeBase is a repository of all the application's knowledge definitions.
 * It will contain rules, processes, functions, type models. The KnowledgeBase itself
 * does not contain data, instead sessions are created from the KnowledgeBase in which
 * data can be inserted and process instances started. Creating the KnowlegeBase can be 
 * heavy, where as session creation is very light, so it is recommended that KnowleBase's
 * be cached where possible to allow for repeated session creation. The KnowledgeBase
 * is created from the KnowledgeBaseFactory:
 * </p>
 * <pre>
 * KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
 * </pre>
 * 
 * 
 */
public interface KnowledgeBase
    extends
    KnowledgeBaseEventManager {

    /**
     * Add the collection of KnowledgePackages to the KnowledgeBase. It is recommended that you
     * ALWAYS check KnowledgeBuilder.hasErrors() first before doing this.
     * @param knowledgePackage
     */
    void addKnowledgePackages(Collection<KnowledgePackage> kpackages);

    /**
     * Return an immutable collection of the packages that exist in this KnowledgeBase.
     * @return
     */
    Collection<KnowledgePackage> getKnowledgePackages();

    /**
     * Remove a KnowledgePackage and all the definitions it contains from the KnowledgeBase.
     * @param packageName
     */
    void removeKnowledgePackage(String packageName);

    /**
     * Remove a rule from the specified package.
     * @param packageName
     * @param ruleName
     */
    void removeRule(String packageName,
                    String ruleName);

    /**
     * Create a new StatefulKnolwedgeSession using the given session configuration.
     * Don't forget to dispose() session when you are done.
     * 
     * @param conf
     * @return
     *     The StatefulKnowledgeSession.
     */
    StatefulKnowledgeSession newStatefulSession(KnowledgeSessionConfiguration conf);

    /**
     * Create a new StatefulKnolwedgeSession using the default session configuration.
     * Don't forget to dispose() session when you are done.
     * 
     * @param conf
     * @return
     *     The StatefulKnowledgeSession.
     */
    StatefulKnowledgeSession newStatefulKnowledgeSession();

    /**
     * Create a new StatelessKnolwedgeSession using the given session configuration.
     * You do not need to call dispose() on this.
     * 
     * @param conf
     * @return
     *     The StatelessKnowledgeSession.
     */
    StatelessKnowledgeSession newStatelessKnowledgeSession(KnowledgeSessionConfiguration conf);

    /**
     * Create a new StatelessKnolwedgeSession using the default session configuration.
     * You do not need to call dispose() on this.
     * 
     * @param conf
     * @return
     *     The StatelessKnowledgeSession.
     */
    StatelessKnowledgeSession newStatelessKnowledgeSession();

}
