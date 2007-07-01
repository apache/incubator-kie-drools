package org.drools.spi;

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

import org.drools.FactException;
import org.drools.FactHandle;
import org.drools.WorkingMemory;
import org.drools.rule.Declaration;
import org.drools.rule.Rule;

/**
 * KnowledgeHelper implementation types are injected into consequenses
 * instrumented at compile time and instances passed at runtime. It provides
 * convenience methods for users to interact with the WorkingMemory.
 * <p>
 * Of particular interest is the update method as it allows an object to
 * be modified without having to specify the facthandle, because they are not
 * passed to the consequence at runtime. To achieve this the implementation will
 * need to lookup the fact handle of the object form the WorkingMemory.
 * 
 * @author <a href="mailto:bob@werken.com">bob mcwhirter </a>
 * @author <a href="mailto:mproctor@codehaus.org">mark proctor</a>
 */
public interface KnowledgeHelper
    extends
    Serializable {
    
    public void setActivation(final Activation agendaItem); 
    
    /**
     * Asserts an object, notice that it does not return the FactHandle
     * 
     * @param object -
     *            the object to be asserted
     * @throws FactException -
     *             Exceptions can be thrown by conditions which are wrapped and
     *             returned as a FactException
     */
    void insert(Object object) throws FactException;

    /**
     * Asserts an object specifying that it implement the onPropertyChange
     * listener, notice that it does not return the FactHandle.
     * 
     * @param object -
     *            the object to be asserted
     * @param dynamic -
     *            specifies the object implements onPropertyChangeListener
     * @throws FactException -
     *             Exceptions can be thrown by conditions which are wrapped and
     *             returned as a FactException
     */
    void insert(Object object,
                      boolean dynamic) throws FactException;

    public void insertLogical(Object object) throws FactException;

    public void insertLogical(Object object,
                                    boolean dynamic) throws FactException;

    void update(FactHandle handle,
                      Object newObject) throws FactException;

    void update( Object newObject ) throws FactException;

    void retract(FactHandle handle) throws FactException;

    void retract(Object object) throws FactException;
    
    public void modifyRetract(final Object object);
    
    public void modifyRetract(final FactHandle factHandle);

    public void modifyInsert(final Object object); 
    
    public void modifyInsert(final FactHandle factHandle,
                             final Object object);     
    
    public Object get(Declaration declaration);

    /**
     * @return - The rule name
     */
    Rule getRule();

    Tuple getTuple();

    Activation getActivation();

    WorkingMemory getWorkingMemory();

//    /** @return - A List of the objects in the WorkingMemory */
//    List getObjects();
//
//    /**
//     * Retruns a List of Objects that match the given Class in the paremeter.
//     * 
//     * @param objectClass -
//     *            The Class to filter by
//     * @return - All the Objects in the WorkingMemory that match the given Class
//     *         filter
//     */
//    List getObjects(Class objectClass);
//
//    QueryResults getQueryResults(String query);
//
//    /**
//     * Clears the agenda causing all existing Activations to fire
//     * ActivationCancelled events. <br>
//     */
//    void clearAgenda();
//
//    void clearAgendaGroup(String group);
//
//    public AgendaGroup getFocus();
//
    void setFocus(String focus);
//
//    void setFocus(AgendaGroup focus);

    public Declaration getDeclaration(String identifier);
    
    public void halt();

}