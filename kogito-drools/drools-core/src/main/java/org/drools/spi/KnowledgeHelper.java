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
import java.util.IdentityHashMap;
import java.util.Map;

import org.drools.FactException;
import org.drools.FactHandle;
import org.drools.WorkingMemory;
import org.drools.rule.Declaration;
import org.drools.rule.Rule;
import org.drools.runtime.ExitPoint;
import org.drools.runtime.rule.RuleContext;
import org.drools.runtime.rule.WorkingMemoryEntryPoint;

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
    RuleContext,
    Serializable {
    
    public void setActivation(final Activation agendaItem); 
    
    public void reset();
    
    
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
    
    public void cancelRemainingPreviousLogicalDependencies();
    
    void update(FactHandle handle,
                      Object newObject) throws FactException;

    void update( Object newObject ) throws FactException;

    void retract(FactHandle handle) throws FactException;

    void retract(Object object) throws FactException;   
    
    public Object get(Declaration declaration);

    /**
     * @return - The rule name
     */
    Rule getRule();

    Tuple getTuple();

    Activation getActivation();

    WorkingMemory getWorkingMemory();
    
    WorkingMemoryEntryPoint getEntryPoint( String id );
    
    Map<String, WorkingMemoryEntryPoint> getEntryPoints();
    
    ExitPoint getExitPoint( String id );
    
    Map<String, ExitPoint> getExitPoints();

    void setFocus(String focus);

    public Declaration getDeclaration(String identifier);
    
    public void halt();

     public IdentityHashMap<Object, FactHandle> getIdentityMap();

    public void setIdentityMap(IdentityHashMap<Object, FactHandle> identityMap);
}