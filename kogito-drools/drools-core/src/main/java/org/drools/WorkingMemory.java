package org.drools;

/*
 * Copyright 2005 JBoss Inc
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

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.drools.event.AgendaEventListener;
import org.drools.event.RuleFlowEventListener;
import org.drools.event.WorkingMemoryEventListener;
import org.drools.ruleflow.common.instance.ProcessInstance;
import org.drools.spi.AgendaFilter;
import org.drools.spi.AgendaGroup;
import org.drools.spi.AsyncExceptionHandler;
import org.drools.spi.GlobalResolver;

/**
 * A knowledge session for a <code>RuleBase</code>.
 * 
 * While this object can be serialised out, it cannot be serialised in. This is because
 * the RuleBase reference is transient. Please see the RuleBase interface for serializing
 * in WorkingMemories from an InputStream.
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
     * Add an event listener.
     * 
     * @param listener
     *            The listener to add.
     */
    public void addEventListener(RuleFlowEventListener listener);

    /**
     * Remove an event listener.
     * 
     * @param listener
     *            The listener to remove.
     */
    public void removeEventListener(RuleFlowEventListener listener);

    /**
     * Returns all event listeners.
     * 
     * @return listeners The listeners.
     */
    public List getAgendaEventListeners();

    public Agenda getAgenda();

    /**
     * Retrieve all of the set application data in this memory
     * 
     * @return the application data as a Map
     */
    Map getGlobals();

    /**
     * Set a specific piece of global in this working memory. Null values will return doing nothign
     * 
     * @param name
     *            the name under which to populate the data
     * @param value
     *            the global value, cannot be null
     */
    void setGlobal(String name,
                   Object value);

    /**
     * Retrieve a specific piece of global data by name
     * 
     * @return application data or null if nothing is set under this name
     */
    Object getGlobal(String name);

    /**
     * Delegate used to resolve any global names not found in the global map.
     * @param globalResolver
     */
    void setGlobalResolver(GlobalResolver globalResolver);

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

    Iterator iterateObjects();
    
    Iterator iterateObjects(ObjectFilter filter);

    Iterator iterateFactHandles();
    
    Iterator iterateFactHandles(ObjectFilter filter);    
    
    public AgendaGroup getFocus();

    void setFocus(String focus);

    void setFocus(AgendaGroup focus);
        

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

    /**
     * Retrieve the QueryResults of the specified query.
     *
     * @param query
     *            The name of the query.
     *
     * @return The QueryResults of the specified query.
     *         If no results match the query it is empty.
     *         
     * @throws IllegalArgumentException 
     *         if no query named "query" is found in the rulebase         
     */
    public QueryResults getQueryResults(String query);
    
    public QueryResults getQueryResults(String query, Object[] arguments);

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
     * Clear the Agenda Group
     */
    public void clearAgendaGroup(String group);

    /**
     * Starts a new process instance for the process with the given id. 
     */
    ProcessInstance startProcess(String processId);
}