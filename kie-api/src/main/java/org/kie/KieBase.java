package org.kie;

import org.kie.definition.KiePackage;
import org.kie.definition.process.Process;
import org.kie.definition.rule.Query;
import org.kie.definition.rule.Rule;
import org.kie.definition.type.FactType;
import org.kie.event.kiebase.KieBaseEventManager;
import org.kie.runtime.Environment;
import org.kie.runtime.KieSession;
import org.kie.runtime.KieSessionConfiguration;
import org.kie.runtime.StatelessKieSession;

import java.util.Collection;
import java.util.Set;

/**
 * <p>
 * The KieBase is a repository of all the application's knowledge definitions.
 * It will contain rules, processes, functions, type models. The KieBase itself
 * does not contain runtime data, instead sessions are created from the KieBase in which
 * data can be inserted and process instances started.
 * </p>
 */
public interface KieBase extends KieBaseEventManager {

    /**
     * Return an immutable collection of the packages that exist in this KieBase.
     * @return
     */
    Collection<KiePackage> getKiePackages();
    
    /**
     * Returns a reference to the KnowledgePackage identified by the given name.
     * 
     * @param packageName the name of the KnowledgePackage to return
     *  
     * @return the KnowledgePackage identified by the the given name or null if package not found.
     */
    KiePackage getKiePackage( String packageName );

    /**
     * Remove a KnowledgePackage and all the definitions it contains from the KnowledgeBase.
     * @param packageName
     */
    void removeKiePackage(String packageName);

    /**
     * Returns a reference to the Rule identified by the given package and rule names.
     * 
     * @param packageName the package name to which the rule belongs to.
     * @param ruleName the name of the rule.
     * 
     * @return the Rule object or null if not found.
     */
    Rule getRule( String packageName, 
                  String ruleName );
    /**
     * Remove a rule from the specified package.
     * @param packageName
     * @param ruleName
     */
    void removeRule(String packageName,
                    String ruleName);
    
    /**
     * Returns a reference to the Rule identified by the given package and rule names.
     * 
     * @param packageName the package name to which the rule belongs to.
     * @param ruleName the name of the rule.
     * 
     * @return the Rule object or null if not found.
     */
    Query getQuery( String packageName, 
                  String queryName );
    /**
     * Remove a rule from the specified package.
     * @param packageName
     * @param ruleName
     */
    void removeQuery(String packageName,
                    String queryName);
    
    /**
     * Remove a function from the specified package.
     * @param packageName
     * @param ruleName
     */
    void removeFunction(String packageName,
                        String ruleName);

    /**
     * Returns the FactType identified by the given package and type names.
     * 
     * @param packageName the name of the package the fact belongs to.
     * @param typeName the name of the type.
     * 
     * @return the FactType identified by the parameters or null if FactType not found.
     */
    FactType getFactType( String packageName, 
                          String typeName );

    /**
     * Returns a referent to the Process identified by the given processId
     * 
     * @param processId the id of the process
     * 
     * @return the Process identified by the given processId or null if process not found.
     */
    Process getProcess( String processId );
    
    /**
     * Remove a process from the specified package.
     * @param processId
     */
    void removeProcess(String processId);
    
    Collection<Process> getProcesses();
    
    /**
     * Create a new StatefulKnowledgeSession using the given session configuration and/or environment.
     * Either one can be null and it will use a default.
     * 
     * Don't forget to dispose() session when you are done.
     * @param conf
     * @param environment
     * @return
     *     The StatefulKnowledgeSession.
     */
    KieSession newKieSession(KieSessionConfiguration conf, Environment environment);

    /**
     * Create a new StatefulKnowledgeSession using the default session configuration.
     * Don't forget to dispose() session when you are done.
     *
     * @return
     *     The StatefulKnowledgeSession.
     */
    KieSession newKieSession();
    
    /**
     * Return a collection of the StatefulKnowledgeSessions that exist in this KnowledgeBase.
     * Be careful as sessions are not thread-safe and could be in use elsewhere.
     * 
     * @return a Collection of StatefulKnowledgeSessions
     */
    Collection<? extends KieSession> getKieSessions();

    /**
     * Create a new StatelessKnowledgeSession using the given session configuration.
     * You do not need to call dispose() on this.
     * 
     * @param conf
     * @return
     *     The StatelessKnowledgeSession.
     */
    StatelessKieSession newStatelessKieSession(KieSessionConfiguration conf);

    /**
     * Create a new StatelessKnowledgeSession using the default session configuration.
     * You do not need to call dispose() on this.
     * 
     * @return
     *     The StatelessKnowledgeSession.
     */
    StatelessKieSession newStatelessKieSession();

    /**
     * Returns the set of the entry points declared and/or used  in this knowledge base
     *  
     * @return
     */
    Set<String> getEntryPointIds();
    
}
