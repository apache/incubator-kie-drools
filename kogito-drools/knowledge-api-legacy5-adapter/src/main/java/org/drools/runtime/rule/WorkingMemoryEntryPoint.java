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

package org.drools.runtime.rule;

import java.util.Collection;

import org.drools.KnowledgeBaseConfiguration;
import org.drools.runtime.ObjectFilter;

/**
 * <p>An entry-point is an abstract channel through where facts are inserted into the engine.</p>
 * <p>Drools 5 supports multiple entry-points into a single <code>StatefulKnowledgeBase</code>: the
 * default, anonymous entry-point, as well as as many user declared entry points the application 
 * requires.</p>
 * 
 * <p>To get a reference to an entry point, just request the session:</p>
 * <pre>
 * StatefulKnowledgeSession session = kbase.newStatelessKnowledgeSession();
 * ...
 * WorkingMemoryEntryPoint entrypoint = session.getWorkingMemoryEntryPoint("my entry point");
 * </pre> 
 * <p>Once a reference to an entry point is acquired, the application can insert, update and retract facts
 * to/from that entry-point as usual:</p>
 * <pre>
 * ...
 * FactHandle factHandle = entrypoint.insert( fact );
 * ...
 * entrypoint.update( factHandle, newFact );
 * ...
 * entrypoint.retract( factHandle );
 * ...
 * </pre> 
 */
public interface WorkingMemoryEntryPoint {

    /**
     * Returns the String Id of this entry point
     * 
     * @return
     */
    public String getEntryPointId();

    /**
     * Inserts a new fact into this entry point
     * 
     * @param object 
     *        the fact to be inserted
     *        
     * @return the fact handle created for the given fact
     */
    FactHandle insert(Object object);

    /**
     * Retracts the fact for which the given FactHandle was assigned.
     * 
     * @param handle the handle whose fact is to be retracted.
     */
    void retract(FactHandle handle);

    /**
     * Updates the fact for which the given FactHandle was assigned with the new
     * fact set as the second parameter in this method.
     *  
     * @param handle the FactHandle for the fact to be updated.
     * 
     * @param object the new value for the fact being updated.
     */
    void update(FactHandle handle,
                Object object);

    /**
     * Returns the fact handle associated with the given object. It is important to note that this 
     * method behaves in accordance with the configured assert behaviour for this knowledge base
     * (either IDENTITY or EQUALITY).
     *  
     * @param object 
     *               the fact for which the fact handle will be returned.
     * 
     * @return the fact handle for the given object, or null in case no fact handle was found for the
     *         given object.
     *         
     * @see KnowledgeBaseConfiguration
     */
    FactHandle getFactHandle(Object object);

    /**
     * Returns the object associated with the given FactHandle.
     * 
     * @param factHandle
     * @return
     */
    Object getObject(FactHandle factHandle);

    /**
     * <p>
     * Returns all facts from the current session as a Collection.
     * </p>
     * 
     * <p>This class is <i>not</i> a general-purpose <tt>Collection</tt>
     * implementation!  While this class implements the <tt>Collection</tt> interface, it
     * intentionally violates <tt>Collection</tt> general contract, which mandates the
     * use of the <tt>equals</tt> method when comparing objects.</p>
     * 
     * <p>Instead the approach used when comparing objects with the <tt>contains(Object)</tt>
     * method is dependent on the WorkingMemory configuration, where it can be configured for <tt>Identity</tt>
     * or for <tt>Equality</tt>.</p> 
     * 
     * @return
     */
    Collection< Object > getObjects();

    /**
     * Returns all facts from the current session that are accepted by the given <code>ObjectFilter</code>.
     * 
     * @param filter the filter to be applied to the returned collection of facts.
     *  
     * @return
     */
    Collection< Object > getObjects(ObjectFilter filter);

    /**
     * Returns all <code>FactHandle</code>s from the current session.
     * 
     * @return
     */
    <T extends FactHandle> Collection< T > getFactHandles();

    /**
     * Returns all <code>FactHandle</code>s from the current session for which the facts are accepted by 
     * the given filter.
     * 
     * @param filter the filter to be applied to the returned collection of <code>FactHandle</code>s.
     * 
     * @return
     */
    <T extends FactHandle> Collection< T > getFactHandles(ObjectFilter filter);
    
    /**
     * Returns the total number of facts currently in this entry point
     * 
     * @return
     */
    public long getFactCount();
    
}
