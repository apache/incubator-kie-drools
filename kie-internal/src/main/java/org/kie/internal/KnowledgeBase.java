/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.internal;

import java.util.Collection;
import java.util.Set;

import org.kie.api.KieBase;
import org.kie.api.definition.process.Process;
import org.kie.api.definition.rule.Query;
import org.kie.api.definition.rule.Rule;
import org.kie.api.definition.type.FactType;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.internal.definition.KnowledgePackage;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.kie.internal.runtime.StatelessKnowledgeSession;


/**
 * Will be removed imminently
 * @deprecated
 *
 */

@Deprecated
public interface KnowledgeBase
    extends
        KieBase {

    /**
     * Add the collection of KnowledgePackages to the KnowledgeBase. It is recommended that you
     * ALWAYS check KnowledgeBuilder.hasErrors() first before doing this.
     * @param kpackages
     */
    void addKnowledgePackages(Collection<KnowledgePackage> kpackages);

    /**
     * Return an immutable collection of the packages that exist in this KnowledgeBase.
     * @return
     */
    Collection<KnowledgePackage> getKnowledgePackages();

    /**
     * Returns a reference to the KnowledgePackage identified by the given name.
     *
     * @param packageName the name of the KnowledgePackage to return
     *
     * @return the KnowledgePackage identified by the the given name or null if package not found.
     */
    KnowledgePackage getKnowledgePackage( String packageName );

    /**
     * Remove a KnowledgePackage and all the definitions it contains from the KnowledgeBase.
     * @param packageName
     */
    void removeKnowledgePackage(String packageName);

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
     * @param queryName the name of the rule.
     *
     * @return the Rule object or null if not found.
     */
    Query getQuery( String packageName,
                  String queryName );
    /**
     * Remove a rule from the specified package.
     * @param packageName
     * @param queryName
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
     * Remove a process.
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
    StatefulKnowledgeSession newStatefulKnowledgeSession(KieSessionConfiguration conf, Environment environment);

    /**
     * Create a new StatefulKnowledgeSession using the default session configuration.
     * Don't forget to dispose() session when you are done.
     *
     * @return
     *     The StatefulKnowledgeSession.
     */
    StatefulKnowledgeSession newStatefulKnowledgeSession();

    /**
     * Return a collection of the StatefulKnowledgeSessions that exist in this KnowledgeBase.
     * Be careful as sessions are not thread-safe and could be in use elsewhere.
     *
     * @return a Collection of StatefulKnowledgeSessions
     */
    Collection<StatefulKnowledgeSession> getStatefulKnowledgeSessions();

    /**
     * Create a new StatelessKnowledgeSession using the given session configuration.
     * You do not need to call dispose() on this.
     *
     * @param conf
     * @return
     *     The StatelessKnowledgeSession.
     */
    StatelessKnowledgeSession newStatelessKnowledgeSession(KieSessionConfiguration conf);

    /**
     * Create a new StatelessKnowledgeSession using the default session configuration.
     * You do not need to call dispose() on this.
     *
     * @return
     *     The StatelessKnowledgeSession.
     */
    StatelessKnowledgeSession newStatelessKnowledgeSession();

    /**
     * Returns the set of the entry points declared and/or used  in this knowledge base
     *
     * @return
     */
    Set<String> getEntryPointIds();

}
