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

package org.drools.base;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;

import org.drools.FactHandle;
import org.drools.WorkingMemory;
import org.drools.common.InternalWorkingMemoryActions;
import org.drools.factmodel.traits.LogicalTypeInconsistencyException;
import org.drools.factmodel.traits.Thing;
import org.drools.factmodel.traits.TraitableBean;
import org.drools.impl.StatefulKnowledgeSessionImpl;
import org.drools.reteoo.ReteooWorkingMemory;
import org.drools.rule.Declaration;
import org.drools.rule.GroupElement;
import org.drools.rule.Rule;
import org.drools.spi.Activation;
import org.drools.spi.KnowledgeHelper;
import org.drools.spi.Tuple;
import org.kie.runtime.Channel;
import org.kie.runtime.KieRuntime;
import org.kie.runtime.KnowledgeRuntime;
import org.kie.runtime.rule.Match;
import org.kie.runtime.rule.SessionEntryPoint;

public class SequentialKnowledgeHelper
    implements
    KnowledgeHelper {

    private static final long                  serialVersionUID = 510l;

    private Rule                               rule;
    private GroupElement                       subrule;
    private Activation                         activation;
    private Tuple                              tuple;
    private final InternalWorkingMemoryActions workingMemory;
    private IdentityHashMap<Object,FactHandle>              identityMap;

    public SequentialKnowledgeHelper(final WorkingMemory workingMemory) {
        this.workingMemory = (InternalWorkingMemoryActions) workingMemory;
    }

    public void setActivation(final Activation agendaItem) {
        this.rule = agendaItem.getRule();
        this.subrule = agendaItem.getSubRule();
        this.activation = agendaItem;
        this.tuple = agendaItem.getTuple();
        this.identityMap = new IdentityHashMap<Object,FactHandle>();
    }
    
    public void reset() {
        this.rule = null;
        this.subrule = null;
        this.activation = null;
        this.tuple = null;
    }
    

    public Rule getRule() {
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
        return new StatefulKnowledgeSessionImpl( (ReteooWorkingMemory) this.workingMemory );
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

    public SessionEntryPoint getEntryPoint(String id) {
        return this.workingMemory.getEntryPoints().get( id );
    }

    public Channel getChannel(String id) {
        return this.workingMemory.getChannels().get( id );
    }

    public Map<String, SessionEntryPoint> getEntryPoints() {
        return Collections.unmodifiableMap( this.workingMemory.getEntryPoints() );
    }

    public Map<String, Channel> getChannels() {
        return Collections.unmodifiableMap( this.workingMemory.getChannels() );
    }

    public IdentityHashMap<Object, FactHandle> getIdentityMap() {
        return this.identityMap;
    }

    public void setIdentityMap(IdentityHashMap<Object, FactHandle> identityMap) {
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

    public <T, K> T don( K core, Class<T> trait) {
        return don( core, trait, false );
    }

    public <T, K> T don(Thing<K> core, Class<T> trait) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public <T,K> Thing<K> shed( Thing<K> thing, Class<T> trait ) {
        return null;
    }

    public <T, K> Thing<K> shed( TraitableBean<K> core, Class<T> trait) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public <T, K> Thing<K> ward(Thing<K> core, Class<T> trait) {
        return null;
    }

    public <T, K> Thing<K> ward(K core, Class<T> trait) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public <T, K> Thing<K> grant(Thing<K> core, Class<T> trait) {
        return null;
    }

    public <T, K> Thing<K> grant(K core, Class<T> trait) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void cancelRemainingPreviousLogicalDependencies() {
    }

    public FactHandle insert(Object object) {
        return null;
    }

    public FactHandle insert(Object object,
                       boolean dynamic) {
        return null;
    }

    public void insertLogical(Object object) {
        // TODO Auto-generated method stub
        
    }

    public void insertLogical(Object object,
                              boolean dynamic) {
        // TODO Auto-generated method stub
        
    }
    
    public void insertLogical(Object object,
                              Object value) {
        // TODO Auto-generated method stub
        
    }

    public FactHandle getFactHandle(Object object) {
        // TODO Auto-generated method stub
        return null;
    }

    public FactHandle getFactHandle(FactHandle handle) {
        // TODO Auto-generated method stub
        return null;
    }

    public void update(FactHandle handle, Object newObject) {
        // TODO Auto-generated method stub
    }

    public void update(FactHandle newObject) {
        // TODO Auto-generated method stub
    }

    public void update(FactHandle newObject, long mask) {
        // TODO Auto-generated method stub
    }

    public void retract(FactHandle handle) {
        // TODO Auto-generated method stub
    }

    public void update(Object newObject) {
        // TODO Auto-generated method stub
    }

    public void update(Object newObject, long mask) {
        // TODO Auto-generated method stub
    }

    public void retract(Object handle) {
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
