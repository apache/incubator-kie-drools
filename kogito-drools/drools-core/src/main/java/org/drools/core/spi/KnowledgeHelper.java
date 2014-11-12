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

import org.drools.core.WorkingMemory;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.factmodel.traits.Thing;
import org.drools.core.factmodel.traits.TraitableBean;
import org.drools.core.rule.Declaration;
import org.drools.core.util.bitmask.BitMask;
import org.kie.api.runtime.Channel;
import org.kie.api.runtime.rule.EntryPoint;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.RuleContext;
import org.kie.internal.runtime.beliefs.Mode;

import java.io.Serializable;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Map;

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
     */
    InternalFactHandle insert(Object object) ;
    
    /**
     * Asserts an object specifying that it implement the onPropertyChange
     * listener
     * 
     * @param object -
     *            the object to be asserted
     * @param dynamic -
     *            specifies the object implements onPropertyChangeListener
     */
    InternalFactHandle insert(Object object,
                boolean dynamic) ;
    
    public InternalFactHandle insertLogical(Object object) ;
    
    public InternalFactHandle insertLogical(Object object,
                              boolean dynamic) ;

    public InternalFactHandle insertLogical(Object object, Mode belief) ;

    public InternalFactHandle insertLogical(Object object, Mode... beliefs) ;
    
    public void cancelRemainingPreviousLogicalDependencies();
    
    InternalFactHandle getFactHandle(Object object);
    
    InternalFactHandle getFactHandle(InternalFactHandle handle);
    
    void update(FactHandle handle, Object newObject);

    void update(FactHandle newObject);
    void update(FactHandle newObject, BitMask mask, Class<?> modifiedClass);
    
    void update(Object newObject);
    void update(Object newObject, BitMask mask, Class<?> modifiedClass);

    void modify( Object newObject ) ;

    /**
     * @deprecated Use delete
     */
    void retract(FactHandle handle) ;

    /**
     * @deprecated Use delete
     */
    void retract(Object handle) ;

    void delete(FactHandle handle) ;

    void delete(Object handle) ;

    public Object get(Declaration declaration);

    /**
     * @return - The rule name
     */
    RuleImpl getRule();

    Tuple getTuple();

    Activation getMatch();

    WorkingMemory getWorkingMemory();
    
    EntryPoint getEntryPoint( String id );
    
    Channel getChannel( String id );
    
    Map<String, Channel> getChannels();

    void setFocus(String focus);

    Declaration getDeclaration(String identifier);
    
    void halt();

    IdentityHashMap<Object, InternalFactHandle> getIdentityMap();

    void setIdentityMap(IdentityHashMap<Object, InternalFactHandle> identityMap);
    
    <T> T getContext(Class<T> contextClass);

    <T, K> T don( K core, Class<T> trait, boolean logical );

    <T, K> T don( Thing<K> core, Class<T> trait, boolean logical );

    <T, K> T don( K core, Class<T> trait, Mode... modes );

    <T, K> T don( Thing<K> core, Class<T> trait, Mode... modes );

    <T, K> T don( K core, Class<T> trait );

    <T, K> T don( Thing<K> core, Class<T> trait );

    <T, K> T don( K core, Collection<Class<? extends Thing>> trait, boolean logical );

    <T, K> T don( K core, Collection<Class<? extends Thing>> trait, Mode... modes );

    <T, K> T don( K core, Collection<Class<? extends Thing>> trait );

    <T, K> Thing<K> shed( Thing<K> thing, Class<T> trait );

    <T, K, X extends TraitableBean> Thing<K> shed( TraitableBean<K,X> core, Class<T> trait );

}
