package org.drools;

/*
 * $Id: WorkingMemory.java,v 1.3 2005/08/14 22:37:04 mproctor Exp $
 *
 * Copyright 2001-2004 (C) The Werken Company. All Rights Reserved.
 *
 * Redistribution and use of this software and associated documentation
 * ("Software"), with or without modification, are permitted provided that the
 * following conditions are met:
 *
 * 1. Redistributions of source code must retain copyright statements and
 * notices. Redistributions must also contain a copy of this document.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. The name "drools" must not be used to endorse or promote products derived
 * from this Software without prior written permission of The Werken Company.
 * For written permission, please contact bob@werken.com.
 *
 * 4. Products derived from this Software may not be called "drools" nor may
 * "drools" appear in their names without prior written permission of The Werken
 * Company. "drools" is a registered trademark of The Werken Company.
 *
 * 5. Due credit should be given to The Werken Company.
 * (http://drools.werken.com/).
 *
 * THIS SOFTWARE IS PROVIDED BY THE WERKEN COMPANY AND CONTRIBUTORS ``AS IS''
 * AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE WERKEN COMPANY OR ITS CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.drools.event.AgendaEventListener;
import org.drools.event.ReteooNodeEventListener;
import org.drools.event.WorkingMemoryEventListener;
import org.drools.spi.AgendaFilter;
import org.drools.spi.AsyncExceptionHandler;
import org.drools.util.LinkedList;

/**
 * A knowledge session for a <code>RuleBase</code>.
 * 
 * @author <a href="mailto:bob@eng.werken.com">bob mcwhirter </a>
 */
public interface WorkingMemory
    extends
    Serializable {
    /**
     * Add an event listener.
     * 
     * @param listener
     *            The listener to add.
     */
    public void addEventListener(WorkingMemoryEventListener listener);

    /**
     * Remove an event listener.
     * 
     * @param listener
     *            The listener to remove.
     */
    public void removeEventListener(WorkingMemoryEventListener listener);

    /**
     * Returns all event listeners.
     * 
     * @return listeners The listeners.
     */
    public List getWorkingMemoryEventListeners();

    /**
     * Add an event listener.
     * 
     * @param listener
     *            The listener to add.
     */
    public void addEventListener(AgendaEventListener listener);

    /**
     * Remove an event listener.
     * 
     * @param listener
     *            The listener to remove.
     */
    public void removeEventListener(AgendaEventListener listener);

    /**
     * Returns all event listeners.
     * 
     * @return listeners The listeners.
     */
    public List getAgendaEventListeners();

    /**
     * Add an event listener.
     * 
     * @param listener
     *            The listener to add.
     */
    public void addEventListener(ReteooNodeEventListener listener);

    /**
     * Remove an event listener.
     * 
     * @param listener
     *            The listener to remove.
     */
    public void removeEventListener(ReteooNodeEventListener listener);

    /**
     * Returns all event listeners.
     * 
     * @return listeners The listeners.
     */
    public List getReteooNodeEventListeners();

    /**
     * Retrieve all of the set application data in this memory
     * 
     * @return the application data as a Map
     */
    Map getGlobals();

    /**
     * Set a specific piece of application data in this working memory
     * 
     * @param name
     *            the name under which to populate the data
     * @param value
     *            the application data
     */
    void setGlobal(String name,
                            Object value);

    /**
     * Retrieve a specific piece of application data by name
     * 
     * @return application data or null if nothing is set under this name
     */
    Object getGlobal(String name);

    /**
     * Retrieve the <code>RuleBase</code> of this working memory.
     * 
     * @return The <code>RuleBase</code>.
     */
    RuleBase getRuleBase();

    /**
     * Fire all items on the agenda until empty.
     * 
     * @throws FactException
     *             If an error occurs.
     */
    void fireAllRules() throws FactException;

    /**
     * Fire all items on the agenda until empty, using the given AgendaFiler
     * 
     * @throws FactException
     *             If an error occurs.
     */
    void fireAllRules(AgendaFilter agendaFilter) throws FactException;

    /**
     * Retrieve the object associated with a <code>FactHandle</code>.
     * 
     * @see #containsObject
     * 
     * @param handle
     *            The fact handle.
     * 
     * @return The associated object.
     * 
     * @throws NoSuchFactObjectException
     *             If no object is known to be associated with the specified
     *             handle.
     */
    Object getObject(FactHandle handle) throws NoSuchFactObjectException;

    /**
     * Retrieve the <code>FactHandle</code> associated with an Object.
     * 
     * @see #containsObject
     * 
     * @param object
     *            The object.
     * 
     * @return The associated fact handle.
     * 
     * @throws NoSuchFactHandleException
     *             If no handle is known to be associated with the specified
     *             object.
     */
    FactHandle getFactHandle(Object object) throws NoSuchFactHandleException;

    /**
     * Retrieve all known objects.
     * 
     * @return The list of all known objects.
     */
    List getObjects();

    /**
     * Retrieve all known objects of the specified class.
     * 
     * @param objectClass
     *            The class of object to return.
     * 
     * @return The list of all known objects of the specified class.
     */
    List getObjects(Class objectClass);

    /**
     * Retrieve all known Fact Handles.
     * 
     * @return The list of all known fact handles.
     */
    List getFactHandles();

    /**
     * Determine if an object is associated with a <code>FactHandle</code>.
     * 
     * @param handle
     *            The fact handle.
     * 
     * @return <code>true</code> if an object is known to be associated with
     *         the specified handle, otherwise <code>false</code>.
     */
    boolean containsObject(FactHandle handle);

    /**
     * Assert a fact.
     * 
     * @param object
     *            The fact object.
     * 
     * @return The new fact-handle associated with the object.
     * 
     * @throws FactException
     *             If an error occurs.
     */
    FactHandle assertObject(Object object) throws FactException;
    
    public LinkedList getQueryResults( String query );

    /**
     * Assert a fact registering JavaBean <code>PropertyChangeListeners</code>
     * on the Object to automatically trigger <code>modifyObject</code> calls
     * if <code>dynamic</code> is <code>true</code>.
     * 
     * @param object
     *            The fact object.
     * @param dynamic
     *            true if Drools should add JavaBean
     *            <code>PropertyChangeListeners</code> to the object.
     * 
     * @return The new fact-handle associated with the object.
     * 
     * @throws FactException
     *             If an error occurs.
     */
    FactHandle assertObject(Object object,
                            boolean dynamic) throws FactException;

    /**
     * Retract a fact.
     * 
     * @param handle
     *            The fact-handle associated with the fact to retract.
     * 
     * @throws FactException
     *             If an error occurs.
     */
    void retractObject(FactHandle handle) throws FactException;

    /**
     * Modify a fact.
     * 
     * @param handle
     *            The fact-handle associated with the fact to modify.
     * @param object
     *            The new value of the fact.
     * 
     * @throws FactException
     *             If an error occurs.
     */
    void modifyObject(FactHandle handle,
                      Object object) throws FactException;

    /**
     * Sets the AsyncExceptionHandler to handle exceptions thrown by the Agenda
     * Scheduler used for duration rules.
     * 
     * @param handler
     */
    void setAsyncExceptionHandler(AsyncExceptionHandler handler);

    /**
     * Clear the Agenda
     * 
     */
    void clearAgenda();

    /**
     * Forces the workingMemory to be derefenced from
     * 
     */
    void dispose();
}
