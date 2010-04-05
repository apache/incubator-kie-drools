package org.drools.base;

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

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;

import org.drools.FactException;
import org.drools.FactHandle;
import org.drools.WorkingMemory;
import org.drools.common.InternalWorkingMemoryActions;
import org.drools.impl.StatefulKnowledgeSessionImpl;
import org.drools.reteoo.ReteooWorkingMemory;
import org.drools.rule.Declaration;
import org.drools.rule.GroupElement;
import org.drools.rule.Rule;
import org.drools.runtime.ExitPoint;
import org.drools.runtime.KnowledgeRuntime;
import org.drools.runtime.rule.WorkingMemoryEntryPoint;
import org.drools.spi.Activation;
import org.drools.spi.KnowledgeHelper;
import org.drools.spi.Tuple;

public class SequentialKnowledgeHelper
    implements
    KnowledgeHelper {

    private static final long                  serialVersionUID = 400L;

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
    

    public void insert(final Object object) throws FactException {        
    }
    
    public void insert(final Object object, final long duration) throws FactException {        
    }

    public void insert(final Object object,
                       final boolean dynamic) throws FactException {
    }
    
    public void insert(final Object object,
    				   final long duration,
    				   final boolean dynamic) throws FactException {
    }

    public void insertLogical(final Object object) throws FactException {
    }
    
    public void insertLogical(final Object object, final long duration) throws FactException {
    }

    public void insertLogical(final Object object,
                              final boolean dynamic) throws FactException {
    }
    
    public void insertLogical(final Object object,
    						  final long duration,
            				  final boolean dynamic) throws FactException {
    }

    public void update(final FactHandle handle,
                       final Object newObject) throws FactException {
    }

    public void update(final Object object) throws FactException {
    }

    public void retract(final FactHandle handle) throws FactException {
    }

    public void retract(final Object object) throws FactException {
    }

    public void modifyRetract(final Object object) {
    }

    public void modifyRetract(final FactHandle factHandle) {
    }

    public void modifyInsert(final Object object) {
    }

    public void modifyInsert(final FactHandle factHandle,
                             final Object object) {      
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

    public Activation getActivation() {
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

    public WorkingMemoryEntryPoint getEntryPoint(String id) {
        return this.workingMemory.getEntryPoints().get( id );
    }

    public ExitPoint getExitPoint(String id) {
        return this.workingMemory.getExitPoints().get( id );
    }

    public Map<String, WorkingMemoryEntryPoint> getEntryPoints() {
        return Collections.unmodifiableMap( this.workingMemory.getEntryPoints() );
    }

    public Map<String, ExitPoint> getExitPoints() {
        return Collections.unmodifiableMap( this.workingMemory.getExitPoints() );
    }

    public IdentityHashMap<Object, FactHandle> getIdentityMap() {
        return this.identityMap;
    }

    public void setIdentityMap(IdentityHashMap<Object, FactHandle> identityMap) {
        this.identityMap = identityMap;
    }

	public <T> T getContext(Class<T> contextClass) {
		// TODO
		return null;
	}

    public void cancelRemainingPreviousLogicalDependencies() {
        // TODO Auto-generated method stub
        
    }

}
