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

package org.drools.core.base;

import org.drools.core.WorkingMemory;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemoryActions;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.factmodel.traits.Thing;
import org.drools.core.factmodel.traits.TraitableBean;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.drools.core.rule.Declaration;
import org.drools.core.rule.GroupElement;
import org.drools.core.spi.Activation;
import org.drools.core.spi.KnowledgeHelper;
import org.drools.core.spi.Tuple;
import org.drools.core.util.bitmask.BitMask;
import org.kie.api.runtime.Channel;
import org.kie.api.runtime.KieRuntime;
import org.kie.api.runtime.rule.EntryPoint;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.Match;
import org.kie.internal.runtime.KnowledgeRuntime;
import org.kie.internal.runtime.beliefs.Mode;

import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;

public class SequentialKnowledgeHelper
    implements
    KnowledgeHelper {

    private static final long                  serialVersionUID = 510l;

    private RuleImpl                           rule;
    private GroupElement                       subrule;
    private Activation                         activation;
    private Tuple                              tuple;
    private final InternalWorkingMemoryActions workingMemory;
    private IdentityHashMap<Object,InternalFactHandle>
                                               identityMap;

    public SequentialKnowledgeHelper(final WorkingMemory workingMemory) {
        this.workingMemory = (InternalWorkingMemoryActions) workingMemory;
    }

    public void setActivation(final Activation agendaItem) {
        this.rule = agendaItem.getRule();
        this.subrule = agendaItem.getSubRule();
        this.activation = agendaItem;
        this.tuple = agendaItem.getTuple();
        this.identityMap = new IdentityHashMap<Object,InternalFactHandle>();
    }
    
    public void reset() {
        this.rule = null;
        this.subrule = null;
        this.activation = null;
        this.tuple = null;
    }
    

    public RuleImpl getRule() {
        return this.rule;
    }

    //    public List getObjects() {
    //        return null; //this.workingMemory.getObjects();
    //    }
    //
    //    public List getObjects(final Class objectClass) {
    //        return null; //this.workingMemory.getObjects( objectClass );
    //    }
    //
    //    public void clearAgenda() {
    //        this.workingMemory.clearAgenda();
    //    }
    //
    //    public void clearAgendaGroup(final String group) {
    //        this.workingMemory.clearAgendaGroup( group );
    //    }


    public Tuple getTuple() {
        return this.tuple;
    }

    public WorkingMemory getWorkingMemory() {
        return this.workingMemory;
    }
    
    public KnowledgeRuntime getKnowledgeRuntime() {
        return (StatefulKnowledgeSessionImpl) this.workingMemory;
     }

    public KieRuntime getKieRuntime() {
        return getKnowledgeRuntime();
     }

    public Activation getMatch() {
        return this.activation;
    }

    //    public QueryResults getQueryResults(final String query) {
    //        return this.workingMemory.getQueryResults( query );
    //    }
    //
    //    public AgendaGroup getFocus() {
    //        return this.workingMemory.getFocus();
    //    }
    //
    public void setFocus(final String focus) {
        this.workingMemory.setFocus( focus );
    }

    //
    //    public void setFocus(final AgendaGroup focus) {
    //        this.workingMemory.setFocus( focus );
    //    }
    
    public Object get(final Declaration declaration) {
        return declaration.getValue( workingMemory, this.tuple.get( declaration ).getObject() );
    }

    public Declaration getDeclaration(final String identifier) {
        return (Declaration) this.subrule.getOuterDeclarations().get( identifier );
    }
    
    public void halt() {
        this.workingMemory.halt();
    }

    public EntryPoint getEntryPoint(String id) {
        return ((StatefulKnowledgeSessionImpl) this.workingMemory).getEntryPoint( id );
    }

    public Channel getChannel(String id) {
        return this.workingMemory.getChannels().get( id );
    }

    public Map<String, Channel> getChannels() {
        return Collections.unmodifiableMap( this.workingMemory.getChannels() );
    }

    public IdentityHashMap<Object, InternalFactHandle> getIdentityMap() {
        return this.identityMap;
    }

    public void setIdentityMap(IdentityHashMap<Object, InternalFactHandle> identityMap) {
        this.identityMap = identityMap;
    }

    public <T> T getContext(Class<T> contextClass) {
        return null;
    }

    public <T, K> T don( K core, Class<T> trait, boolean logical ) {
        return null;
    }

    public <T, K> T don(Thing<K> core, Class<T> trait, boolean logical) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <T, K> T don( K core, Class<T> trait, Mode... modes ) {
        return null;
    }

    @Override
    public <T, K> T don( Thing<K> core, Class<T> trait, Mode... modes ) {
        return null;
    }

    public <T, K> T don( K core, Class<T> trait) {
        return don( core, trait, false );
    }

    public <T, K> T don(Thing<K> core, Class<T> trait) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <T, K> T don( K core, Collection<Class<? extends Thing>> trait, boolean logical ) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <T, K> T don( K core, Collection<Class<? extends Thing>> trait, Mode... modes ) {
        return null;
    }

    @Override
    public <T, K> T don( K core, Collection<Class<? extends Thing>> trait ) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public <T,K> Thing<K> shed( Thing<K> thing, Class<T> trait ) {
        return null;
    }

    public <T, K, X extends TraitableBean> Thing<K> shed( TraitableBean<K,X> core, Class<T> trait) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void cancelRemainingPreviousLogicalDependencies() {
    }

    public InternalFactHandle insert(Object object) {
        return null;
    }

    public InternalFactHandle insert(Object object,
                       boolean dynamic) {
        return null;
    }

    @Override
    public InternalFactHandle insertLogical(Object object, Mode belief) {
        return null;
    }

    @Override
    public InternalFactHandle insertLogical(Object object, Mode... beliefs) {
        return null;
    }

    public InternalFactHandle insertLogical(Object object) {
        // TODO Auto-generated method stub
        return null;
    }

    public InternalFactHandle insertLogical(Object object,
                                    boolean dynamic) {
        // TODO Auto-generated method stub
        return null;
    }
    
    public InternalFactHandle insertLogical(Object object,
                                    Object value) {
        // TODO Auto-generated method stub
        return null;
    }

    public InternalFactHandle getFactHandle(Object object) {
        // TODO Auto-generated method stub
        return null;
    }

    public InternalFactHandle getFactHandle(InternalFactHandle handle) {
        // TODO Auto-generated method stub
        return null;
    }

    public void update(FactHandle handle, Object newObject) {
        // TODO Auto-generated method stub
    }

    public void update(FactHandle newObject) {
        // TODO Auto-generated method stub
    }

    public void update(FactHandle newObject, BitMask mask, Class<?> typeClass) {
        // TODO Auto-generated method stub
    }

    public void retract(FactHandle handle) {
        // TODO Auto-generated method stub
    }

    public void delete(FactHandle handle) {
        // TODO Auto-generated method stub
    }

    public void update(Object newObject) {
        // TODO Auto-generated method stub
    }

    public void update(Object newObject, BitMask mask, Class<?> typeClass) {
        // TODO Auto-generated method stub
    }

    public void retract(Object handle) {
        // TODO Auto-generated method stub
    }

    public void delete(Object handle) {
        // TODO Auto-generated method stub
    }

    public void modify(Object newObject) {
        // TODO Auto-generated method stub
    }

    public void blockMatch(Match match) {
        // TODO Auto-generated method stub
    }

    public void unblockAllMatches(Match match) {
        // TODO Auto-generated method stub
    }

    public void cancelMatch(Match match) {
        // TODO Auto-generated method stub
    }
}
