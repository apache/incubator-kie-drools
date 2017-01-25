/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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

package org.kie.api;

import java.util.Collection;
import java.util.Set;

import org.kie.api.definition.KiePackage;
import org.kie.api.definition.process.Process;
import org.kie.api.definition.rule.Query;
import org.kie.api.definition.rule.Rule;
import org.kie.api.definition.type.FactType;
import org.kie.api.event.kiebase.KieBaseEventManager;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.StatelessKieSession;

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
     * Returns a collection of the {@link KiePackage}s that exist in this {@link KieBase}.
     *
     * @return an immutable collection of the packages
     */
    Collection<KiePackage> getKiePackages();

    /**
     * Returns a reference to the {@link KiePackage} identified by the given name.
     *
     * @param packageName the name of the {@link KiePackage} to return
     *
     * @return the {@link KiePackage} identified by the the given name or null if package not found.
     */
    KiePackage getKiePackage( String packageName );

    /**
     * Removes a {@link KiePackage} and all the definitions it contains from the {@link KieBase}
     *
     * @param packageName the name of the {@link KiePackage} to remove
     */
    void removeKiePackage( String packageName );

    /**
     * Returns a reference to the {@link Rule} identified by the given package and rule names.
     *
     * @param packageName the package name to which the rule belongs to
     * @param ruleName the name of the rule
     *
     * @return the {@link Rule} object or null if not found
     */
    Rule getRule( String packageName,
                  String ruleName );
    /**
     * Removes a rule from the specified package.
     *
     * @param packageName the package name to which the rule belongs to
     * @param ruleName the name of the rule
     */
    void removeRule( String packageName,
                     String ruleName );

    /**
     * Returns a reference to the {@link Query} identified by the given package and query names.
     *
     * @param packageName the package name to which the query belongs to
     * @param queryName the name of the query
     *
     * @return the {@link Query} object or null if not found.
     */
    Query getQuery( String packageName,
                    String queryName );
    /**
     * Removes a query from the specified package.
     *
     * @param packageName the package name to which the query belongs to
     * @param queryName the name of the query
     */
    void removeQuery( String packageName,
                      String queryName );

    /**
     * Removes a function from the specified package.
     *
     * @param packageName the package name to which the function belongs to
     * @param functionName the name of the function
     */
    void removeFunction( String packageName,
                         String functionName );

    /**
     * Returns a reference to the {@link FactType} identified by the given package and type names.
     *
     * @param packageName the name of the package the fact belongs to
     * @param typeName the name of the type
     *
     * @return the {@link FactType} identified by the parameters or null if not found.
     */
    FactType getFactType( String packageName,
                          String typeName );

    /**
     * Returns a reference to the {@link Process} identified by the given processId
     *
     * @param processId the id of the process
     *
     * @return the {@link Process} identified by the given processId or null if process not found.
     */
    Process getProcess( String processId );

    /**
     * Removes a process.
     *
     * @param processId the id of the process
     */
    void removeProcess( String processId );

    /**
     * Returns a collection of the {@link Process}es that exist in this {@link KieBase}.
     *
     * @return an immutable collection of the processes
     */
    Collection<Process> getProcesses();

    /**
     * Creates a new {@link KieSession} using the given session configuration and/or environment.
     * Either one can be null and it will use a default.
     *
     * Don't forget to {@link KieSession#dispose()} session when you are done.
     * @param conf session configuration
     * @param environment environment
     *
     * @return created {@link KieSession}
     */
    KieSession newKieSession( KieSessionConfiguration conf, Environment environment );

    /**
     * Creates a new {@link KieSession} using the default session configuration.
     * Don't forget to {@link KieSession#dispose()} session when you are done.
     *
     * @return created {@link KieSession}
     */
    KieSession newKieSession();

    /**
     * Returns a collection of the {@link KieSession}s that exist in this {@link KieBase}.
     * Be careful as sessions are not thread-safe and could be in use elsewhere.
     *
     * @return a Collection of {@link KieSession}s
     */
    Collection<? extends KieSession> getKieSessions();

    /**
     * Creates a new {@link StatelessKieSession} using the given session configuration.
     * You do not need to call {@link KieSession#dispose()} on this.
     *
     * @param conf session configuration
     *
     * @return created {@link StatelessKieSession}
     */
    StatelessKieSession newStatelessKieSession( KieSessionConfiguration conf );

    /**
     * Creates a new {@link StatelessKieSession} using the default session configuration.
     * You do not need to call @{link #dispose()} on this.
     *
     * @return created {@link StatelessKieSession}
     */
    StatelessKieSession newStatelessKieSession();

    /**
     * Returns the set of the entry points declared and/or used  in this kie base
     *
     * @return a Set of entry points
     */
    Set<String> getEntryPointIds();

}
