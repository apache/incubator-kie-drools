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

package org.kie.api.runtime.rule;

import org.kie.api.runtime.ObjectFilter;

import java.util.Collection;

/**
 * <p>An entry-point is an abstract channel through where facts are inserted into the engine.</p>
 * <p>KIE 6 supports multiple entry-points into a single {@link org.kie.api.KieBase}: the
 * default, anonymous entry-point, as well as as many user declared entry points the application 
 * requires.</p>
 * 
 * <p>To get a reference to an entry point, just request the session:</p>
 * <pre>
 * KieSession session = kbase.newStatelessKieSession();
 * ...
 * WorkingMemoryEntryPoint entrypoint = session.getEntryPoint("my entry point");
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
public interface EntryPoint {

    /**
     * @return the String Id of this entry point
     */
    String getEntryPointId();

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
     * @deprecated use {@link #delete(FactHandle)}
     */
    void retract(FactHandle handle);

    /**
     * Retracts the fact for which the given FactHandle was assigned
     * regardless if it has been explicitly or logically inserted.
     * 
     * @param handle the handle whose fact is to be retracted.
     */
    void delete(FactHandle handle);

    /**
     * Retracts the fact for which the given FactHandle was assigned.
     *
     * @param handle the handle whose fact is to be retracted.
     * @param fhState defines how the handle has to be retracted:
     *                as an explicitly inserted fact (STATED), from the
     *                Truth Maintenance System (LOGICAL), or both (ALL)
     */
    void delete(FactHandle handle, FactHandle.State fhState);

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
     * method behaves in accordance with the configured assert behaviour for this {@link org.kie.api.KieBase}
     * (either IDENTITY or EQUALITY).
     *  
     * @param object 
     *               the fact for which the fact handle will be returned.
     * 
     * @return the fact handle for the given object, or null in case no fact handle was found for the
     *         given object.
     *         
     * @see org.kie.api.KieBaseConfiguration
     */
    FactHandle getFactHandle(Object object);

    /**
     * @return the object associated with the given FactHandle.
     */
    Object getObject(FactHandle factHandle);

    /**
     * <p>This class is <i>not</i> a general-purpose <tt>Collection</tt>
     * implementation!  While this class implements the <tt>Collection</tt> interface, it
     * intentionally violates <tt>Collection</tt> general contract, which mandates the
     * use of the <tt>equals</tt> method when comparing objects.</p>
     * 
     * <p>Instead the approach used when comparing objects with the <tt>contains(Object)</tt>
     * method is dependent on the WorkingMemory configuration, where it can be configured for <tt>Identity</tt>
     * or for <tt>Equality</tt>.</p> 
     * 
     * @return all facts from the current session as a Collection.
     */
    Collection<? extends Object> getObjects();

    /**
     * @param filter the filter to be applied to the returned collection of facts.
     * @return all facts from the current session that are accepted by the given <code>ObjectFilter</code>.
     */
    Collection<? extends Object> getObjects(ObjectFilter filter);

    /**
     * @return all <code>FactHandle</code>s from the current session.
     */
    <T extends FactHandle> Collection< T > getFactHandles();

    /**
     * @param filter the filter to be applied to the returned collection of <code>FactHandle</code>s.
     * @return all <code>FactHandle</code>s from the current session for which the facts are accepted by
     * the given filter.
     */
    <T extends FactHandle> Collection< T > getFactHandles(ObjectFilter filter);
    
    /**
     * @return the total number of facts currently in this entry point
     */
    long getFactCount();
    
}
