/**
 * Copyright 2010 JBoss Inc
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

package org.drools.runtime.rule;

import java.util.Collection;

/**
 * The <code>WorkingMemory</code> is a super-interface for all <code>StatefulKnowledgeSession</code>s.
 * Although, users are encouraged to use <code>StatefulKnowledgeSession</code> or <code>KnowledgeRuntime</code>
 * interface instead of <code>WorkingMemory</code> interface, specially because of the <code>dispose()</code> method
 * that is only available in the <code>StatefulKnowledgeSession</code> interface.  
 * 
 * @see org.drools.runtime.StatefulKnowledgeSession 
 */
public interface WorkingMemory
    extends
    WorkingMemoryEntryPoint {

    /**
     * <p>Request the engine to stop firing rules. If the engine is currently firing a rule, it will
     * finish executing this rule's consequence before stopping.</p>
     * <p>This method will not remove active activations from the Agenda.
     * In case the application later wants to continue firing rules from the point where it stopped,
     * it should just call <code>org.drools.runtime.StatefulKnowledgeSession.fireAllRules()</code> or 
     * <code>org.drools.runtime.StatefulKnowledgeSession.fireUntilHalt()</code> again.</p>
     */
    void halt();

    /**
     * Returns a reference to this session's <code>Agenda</code>.
     * 
     * @return
     */
    Agenda getAgenda();

    /**
     * Returns the WorkingMemoryEntryPoint instance associated with the given name.
     * 
     * @param name
     * @return
     */
    WorkingMemoryEntryPoint getWorkingMemoryEntryPoint(String name);

    /**
     * Returns a collection of all available working memory entry points
     * for this session.
     * 
     * @return the collection of all available entry points for this session
     */
    Collection< ? extends WorkingMemoryEntryPoint> getWorkingMemoryEntryPoints();

    /**
     * Retrieve the QueryResults of the specified query and arguments
     *
     * @param query
     *            The name of the query.
     *
     * @param arguments
     *            The arguments used for the query
     *
     * @return The QueryResults of the specified query.
     *         If no results match the query it is empty.
     *
     * @throws IllegalArgumentException
     *         if no query named "query" is found in the KnowledgeBase
     */
    public QueryResults getQueryResults(String query,
                                        Object... arguments);

    public LiveQuery openLiveQuery(String query,
                                   Object[] arguments,
                                   ViewChangedEventListener listener);
}
