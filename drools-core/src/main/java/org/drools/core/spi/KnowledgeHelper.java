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

package org.drools.core.spi;

import java.io.Serializable;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Map;

import org.drools.core.FactHandle;
import org.drools.core.WorkingMemory;
import org.drools.core.factmodel.traits.Thing;
import org.drools.core.factmodel.traits.TraitableBean;
import org.drools.core.rule.Declaration;
import org.drools.core.rule.Rule;
import org.kie.api.runtime.Channel;
import org.kie.api.runtime.rule.RuleContext;
import org.kie.api.runtime.rule.EntryPoint;

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
     * Asserts an object
     * 
     * @param object -
     *            the object to be asserted
     * @ -
     *             Exceptions can be thrown by conditions which are wrapped and
     *             returned as a FactException
     */
    FactHandle insert(Object object) ;
    
    /**
     * Asserts an object specifying that it implement the onPropertyChange
     * listener
     * 
     * @param object -
     *            the object to be asserted
     * @param dynamic -
     *            specifies the object implements onPropertyChangeListener
     * @ -
     *             Exceptions can be thrown by conditions which are wrapped and
     *             returned as a FactException
     */
    FactHandle insert(Object object,
                boolean dynamic) ;
    
    public void insertLogical(Object object) ;
    
    public void insertLogical(Object object,
                              boolean dynamic) ;
    
    public void cancelRemainingPreviousLogicalDependencies();
    
    FactHandle getFactHandle(Object object);
    
    FactHandle getFactHandle(FactHandle handle);
    
    void update(FactHandle handle, Object newObject);

    void update(FactHandle newObject);
    void update(FactHandle newObject, long mask, Class<?> modifiedClass);
    
    void update(Object newObject);
    void update(Object newObject, long mask, Class<?> modifiedClass);

    void modify( Object newObject ) ;

    void retract(FactHandle handle) ;
    
    void retract(Object handle) ;
    
    public Object get(Declaration declaration);

    /**
     * @return - The rule name
     */
    Rule getRule();

    Tuple getTuple();

    Activation getMatch();

    WorkingMemory getWorkingMemory();
    
    EntryPoint getEntryPoint( String id );
    
    Map<String, EntryPoint> getEntryPoints();
    
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

    <T, K> T don( K core, Collection<Class<? extends Thing>> trait, boolean logical );

    <T, K> T don( K core, Collection<Class<? extends Thing>> trait );

    <T, K> Thing<K> shed( Thing<K> thing, Class<T> trait );

    <T, K, X extends TraitableBean> Thing<K> shed( TraitableBean<K,X> core, Class<T> trait );

}
