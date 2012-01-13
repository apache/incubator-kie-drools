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

package org.drools.spi;

import java.io.Serializable;
import java.util.IdentityHashMap;
import java.util.Map;

import org.drools.FactHandle;
import org.drools.WorkingMemory;
import org.drools.factmodel.traits.Thing;
import org.drools.factmodel.traits.TraitableBean;
import org.drools.rule.Declaration;
import org.drools.rule.Rule;
import org.drools.runtime.Channel;
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
     * @ -
     *             Exceptions can be thrown by conditions which are wrapped and
     *             returned as a FactException
     */
    void insert(Object object) ;
    
    /**
     * Asserts an object specifying that it implement the onPropertyChange
     * listener, notice that it does not return the FactHandle.
     * 
     * @param object -
     *            the object to be asserted
     * @param dynamic -
     *            specifies the object implements onPropertyChangeListener
     * @ -
     *             Exceptions can be thrown by conditions which are wrapped and
     *             returned as a FactException
     */
    void insert(Object object,
                      boolean dynamic) ;
    
    public void insertLogical(Object object) ;
    
    public void insertLogical(Object object,
                                    boolean dynamic) ;
    
    public void cancelRemainingPreviousLogicalDependencies();
    
    FactHandle getFactHandle(Object object);
    
    FactHandle getFactHandle(FactHandle handle);
    
    void update(FactHandle handle, Object newObject);

    void update(FactHandle newObject);
    void update(FactHandle newObject, long mask);
    
    void update(Object newObject);
    void update(Object newObject, long mask);

    void modify( Object newObject ) ;

    void retract(FactHandle handle) ;
    
    void retract(Object handle) ;
    
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
    
    /**
     * @deprecated Use {@link #getChannel(String)} instead.
     */
    @Deprecated
    ExitPoint getExitPoint( String id );
    
    /**
     * @deprecated Use {@link #getChannels()} instead.
     */
    @Deprecated
    Map<String, ExitPoint> getExitPoints();
    
    Channel getChannel( String id );
    
    Map<String, Channel> getChannels();

    void setFocus(String focus);

    Declaration getDeclaration(String identifier);
    
    void halt();

    IdentityHashMap<Object, FactHandle> getIdentityMap();

    void setIdentityMap(IdentityHashMap<Object, FactHandle> identityMap);
    
    <T> T getContext(Class<T> contextClass);

    <T, K> T don( K core, Class<T> trait, boolean logical );

    <T, K> T don( Thing<K> core, Class<T> trait, boolean logical );

    <T, K> T don( K core, Class<T> trait );

    <T, K> T don( Thing<K> core, Class<T> trait );

    <T,K> Thing<K> shed( Thing<K> thing, Class<T> trait );

    <T,K> Thing<K> shed( TraitableBean<K> core, Class<T> trait );
}
