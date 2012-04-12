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

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;

import org.drools.FactException;
import org.drools.FactHandle;
import org.drools.WorkingMemory;
import org.drools.common.AgendaItem;
import org.drools.common.DefaultAgenda;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalRuleFlowGroup;
import org.drools.common.InternalWorkingMemoryActions;
import org.drools.common.InternalWorkingMemoryEntryPoint;
import org.drools.common.LogicalDependency;
import org.drools.core.util.LinkedList;
import org.drools.core.util.LinkedListEntry;
import org.drools.factmodel.traits.CoreWrapper;
import org.drools.factmodel.traits.Thing;
import org.drools.factmodel.traits.TraitableBean;
import org.drools.factmodel.traits.TraitFactory;
import org.drools.reteoo.LeftTuple;
import org.drools.reteoo.ReteooWorkingMemory;
import org.drools.rule.Declaration;
import org.drools.rule.Rule;
import org.drools.runtime.Channel;
import org.drools.runtime.ExitPoint;
import org.drools.runtime.KnowledgeRuntime;
import org.drools.runtime.process.NodeInstance;
import org.drools.runtime.process.NodeInstanceContainer;
import org.drools.runtime.process.ProcessContext;
import org.drools.runtime.process.ProcessInstance;
import org.drools.runtime.process.WorkflowProcessInstance;
import org.drools.runtime.rule.WorkingMemoryEntryPoint;
import org.drools.spi.Activation;
import org.drools.spi.KnowledgeHelper;
import org.drools.spi.PropagationContext;
import org.drools.spi.Tuple;

public class DefaultKnowledgeHelper
    implements
    KnowledgeHelper,
    Externalizable {

    private static final long                   serialVersionUID = 510l;

    private Activation                          activation;
    private Tuple                               tuple;
    private InternalWorkingMemoryActions        workingMemory;

    private IdentityHashMap<Object, FactHandle> identityMap;

    private LinkedList                          previousJustified;
    
    private LinkedList                          previousBlocked;

    public DefaultKnowledgeHelper() {

    }

    public DefaultKnowledgeHelper(final WorkingMemory workingMemory) {
        this.workingMemory = (InternalWorkingMemoryActions) workingMemory;

        this.identityMap = null;

    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        activation = (Activation) in.readObject();
        tuple = (Tuple) in.readObject();
        workingMemory = (InternalWorkingMemoryActions) in.readObject();
        identityMap = (IdentityHashMap<Object, FactHandle>) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject( activation );
        out.writeObject( tuple );
        out.writeObject( workingMemory );
        out.writeObject( identityMap );
    }

    public void setActivation(final Activation agendaItem) {
        this.activation = agendaItem;
        // -- JBRULES-2558: logical inserts must be properly preserved
        this.previousJustified = agendaItem.getLogicalDependencies();
        this.previousBlocked = agendaItem.getBlocked();
        agendaItem.setLogicalDependencies( null );
        agendaItem.setBlocked( null );
        // -- JBRULES-2558: end
        this.tuple = agendaItem.getTuple();
    }

    public void reset() {
        this.activation = null;
        this.tuple = null;
        this.identityMap = null;
        this.previousJustified = null;
        this.previousBlocked = null;
    }
      
    public void blockActivation(org.drools.runtime.rule.Activation act) {
        AgendaItem targetMatch = ( AgendaItem ) act;
        // iterate to find previous equal logical insertion
        LogicalDependency dep = null;
        if ( this.previousJustified != null ) {
            for ( dep = (LogicalDependency) this.previousJustified.getFirst(); dep != null; dep = (LogicalDependency) dep.getNext() ) {
                if ( targetMatch ==  dep.getJustified() ) {
                    this.previousJustified.remove( dep );
                    break;
                }
            }
        }
        
        if ( dep == null ) {
            dep = new LogicalDependency( activation, targetMatch );
        }
        this.activation.addBlocked(  dep );
        
        if ( targetMatch.getBlockers().size() == 1 && targetMatch.isActive()  ) {
            // it wasn't blocked before, but is now, so we must remove it from all groups, so it cannot be executed.
            targetMatch.remove();

            if ( targetMatch.getActivationGroupNode() != null ) {
                targetMatch.getActivationGroupNode().getActivationGroup().removeActivation( targetMatch );
            }

            if ( targetMatch.getActivationNode() != null ) {
                final InternalRuleFlowGroup ruleFlowGroup = (InternalRuleFlowGroup) targetMatch.getActivationNode().getParentContainer();
                ruleFlowGroup.removeActivation( targetMatch );
            }
        }
    }
    
    public void unblockAllActivations(org.drools.runtime.rule.Activation act) {
        AgendaItem targetMatch = ( AgendaItem ) act;
        boolean wasBlocked = (targetMatch.getBlockers() != null && !targetMatch.getBlockers().isEmpty() );
        
        for ( LinkedListEntry entry = ( LinkedListEntry ) targetMatch.getBlockers().getFirst(); entry != null;  ) {
            LinkedListEntry tmp = ( LinkedListEntry ) entry.getNext();
            LogicalDependency dep = ( LogicalDependency ) entry.getObject();
            ((AgendaItem)dep.getJustifier()).removeBlocked( dep );
            entry = tmp;
        }
        
        if ( wasBlocked ) {
            // the match is no longer blocked, so stage it
            ((DefaultAgenda)workingMemory.getAgenda()).getStageActivationsGroup().addActivation( targetMatch );
        }
    }

    public void insert(final Object object) {
        insert( object,
                false );
    }

    public void insert(final Object object,
                       final boolean dynamic) throws FactException {
        FactHandle handle = this.workingMemory.insert( object,
                                                       dynamic,
                                                       false,
                                                       this.activation.getRule(),
                                                       this.activation );
        if ( this.identityMap != null ) {
            this.getIdentityMap().put( object,
                                       handle );
        }
    }

    public void insertLogical(final Object object) {
        insertLogical( object,
                       false );
    }

    public void insertLogical(final Object object,
                              final boolean dynamic) {
        
        if ( !activation.isMatched() ) {
            // Activation is already unmatched, can't do logical insertions against it
            return;
        }
        // iterate to find previous equal logical insertion
        LogicalDependency dep = null;
        if ( this.previousJustified != null ) {
            for ( dep = (LogicalDependency) this.previousJustified.getFirst(); dep != null; dep = (LogicalDependency) dep.getNext() ) {
                if ( object.equals( ((InternalFactHandle) dep.getJustified()).getObject() ) ) {
                    this.previousJustified.remove( dep );
                    break;
                }
            }
        }

        if ( dep != null ) {
            // Add the previous matching logical dependency back into the list
            this.activation.addLogicalDependency( dep );
        } else {
            // no previous matching logical dependency, so create a new one
            FactHandle handle = this.workingMemory.insert( object,
                                                           dynamic,
                                                           true,
                                                           this.activation.getRule(),
                                                           this.activation );

            if ( this.identityMap != null ) {
                this.getIdentityMap().put( object,
                                           handle );
            }
        }
    }
    
    public void cancelRemainingPreviousLogicalDependencies() {
        if ( this.previousJustified != null ) {
            for ( LogicalDependency dep = (LogicalDependency) this.previousJustified.getFirst(); dep != null; dep = (LogicalDependency) dep.getNext() ) {
                this.workingMemory.getTruthMaintenanceSystem().removeLogicalDependency( activation, dep, activation.getPropagationContext() );
            }
        }
        
        if ( this.previousBlocked != null ) {
            for ( LogicalDependency dep = (LogicalDependency) this.previousBlocked.getFirst(); dep != null; ) {
                LogicalDependency tmp = ( LogicalDependency ) dep.getNext();
                this.previousBlocked.remove( dep );
                
                AgendaItem justified = ( AgendaItem ) dep.getJustified();
                justified.getBlockers().remove( dep.getJustifierEntry() );
                if (justified.getBlockers().isEmpty() ) {
                    // the match is no longer blocked, so stage it
                    ((DefaultAgenda)workingMemory.getAgenda()).getStageActivationsGroup().addActivation( justified );
                }
                dep = tmp;
            }
        }        
    }
    
    public void cancelActivation(org.drools.runtime.rule.Activation act) {
        AgendaItem match = ( AgendaItem ) act;
        match.cancel();
        if ( match.isActive() ) {
            LeftTuple leftTuple = match.getTuple();
            leftTuple.getLeftTupleSink().retractLeftTuple( leftTuple, (PropagationContext) act.getPropagationContext(), workingMemory );
        }
    }
    
    public FactHandle getFactHandle(Object object) {
        FactHandle handle = null;
        if ( identityMap != null ) {
            handle = identityMap.get( object );
        }
        
        if ( handle != null ) {
            return handle;
        }
        
        handle = getFactHandleFromWM( object );
        
        if ( handle == null ) {
            throw new FactException( "Update error: handle not found for object: " + object + ". Is it in the working memory?" );
        }
        return handle;
    }
    
    public FactHandle getFactHandle(FactHandle handle) {
        Object object = ((InternalFactHandle)handle).getObject();
        handle = getFactHandleFromWM( object );
        if ( handle == null ) {
            throw new FactException( "Update error: handle not found for object: " + object + ". Is it in the working memory?" );
        }
        return handle;
    }
    
    public void update(final FactHandle handle,
                       final Object newObject){
        ((InternalWorkingMemoryEntryPoint) ((InternalFactHandle) handle).getEntryPoint()).update( handle,
                                                                                                  newObject,
                                                                                                  this.activation.getRule(),
                                                                                                  this.activation );
        if ( getIdentityMap() != null ) {
            this.getIdentityMap().put( newObject,
                                       handle );
        }
    }
    
    public void update(final FactHandle handle) {
        ((InternalWorkingMemoryEntryPoint) ((InternalFactHandle) handle).getEntryPoint()).update( handle,
                                                                                                  ((InternalFactHandle)handle).getObject(),
                                                                                                  this.activation.getRule(),
                                                                                                  this.activation );
    }

    
    public void update( Object object ) {
        update( getFactHandle(object) );
    }
    
    public void retract(Object object) {
       retract( getFactHandle(object) );
    }

    public void retract(final FactHandle handle) {
        ((InternalWorkingMemoryEntryPoint) ((InternalFactHandle) handle).getEntryPoint()).retract( handle,
                                                                                                   true,
                                                                                                   true,
                                                                                                   this.activation.getRule(),
                                                                                                   this.activation );
        if ( this.identityMap != null ) {
            this.getIdentityMap().remove( ((InternalFactHandle) handle).getObject() );
        }
    }

    public Rule getRule() {
        return this.activation.getRule();
    }

    public Tuple getTuple() {
        return this.tuple;
    }

    public WorkingMemory getWorkingMemory() {
        return this.workingMemory;
    }

    public KnowledgeRuntime getKnowledgeRuntime() {
        return ((ReteooWorkingMemory) this.workingMemory).getKnowledgeRuntime();
    }

    public Activation getActivation() {
        return this.activation;
    }

    public void setFocus(final String focus) {
        this.workingMemory.setFocus( focus );
    }

    public Object get(final Declaration declaration) {
        InternalWorkingMemoryEntryPoint wmTmp = ((InternalWorkingMemoryEntryPoint) (this.tuple.get( declaration )).getEntryPoint());

        if ( wmTmp != null ) {
            Object object = declaration.getValue( wmTmp.getInternalWorkingMemory(),
                                                  this.tuple.get( declaration ).getObject() );
            
            if ( identityMap != null ) {
                getIdentityMap().put( object,
                                      wmTmp.getFactHandleByIdentity( object ) );
            }
            return object;
        }
        return null;
    }

    public Declaration getDeclaration(final String identifier) {
        return (Declaration) ((AgendaItem)this.activation).getRuleTerminalNode().getSubRule().getOuterDeclarations().get( identifier );
    }

    public void halt() {
        this.workingMemory.halt();
    }

    public WorkingMemoryEntryPoint getEntryPoint(String id) {
        return this.workingMemory.getEntryPoints().get( id );
    }

    /**
     * @deprecated use {@link #getChannel(String)} instead
     */
    @Deprecated
    public ExitPoint getExitPoint(String id) {
        return this.workingMemory.getExitPoints().get( id );
    }
    
    public Channel getChannel(String id) {
        return this.workingMemory.getChannels().get( id );
    }

    public Map<String, WorkingMemoryEntryPoint> getEntryPoints() {
        return Collections.unmodifiableMap( this.workingMemory.getEntryPoints() );
    }

    /**
     * @deprecated use {@link #getChannels()} instead
     */
    @Deprecated
    public Map<String, ExitPoint> getExitPoints() {
        return Collections.unmodifiableMap( this.workingMemory.getExitPoints() );
    }
    
    public Map<String, Channel> getChannels() {
        return Collections.unmodifiableMap( this.workingMemory.getChannels() );
    }

    /**
     * @return the identityMap
     */
    public IdentityHashMap<Object, FactHandle> getIdentityMap() {
        return identityMap;
    }

    /**
     * @param identityMap the identityMap to set
     */
    public void setIdentityMap(IdentityHashMap<Object, FactHandle> identityMap) {
        this.identityMap = identityMap;
    }

    private FactHandle getFactHandleFromWM(final Object object) {
        FactHandle handle = null;
        // entry point null means it is a generated fact, not a regular inserted fact
        // NOTE: it would probably be a good idea to create a specific attribute for that
            for ( WorkingMemoryEntryPoint ep : workingMemory.getEntryPoints().values() ) {
                handle = (FactHandle) ep.getFactHandle( object );
                if ( identityMap != null ) {
                    identityMap.put( object,
                                     handle );
                }
                if( handle != null ) {
                    break;
                }
            }
        return handle;
    }
    
    @SuppressWarnings("unchecked")
    public <T> T getContext(Class<T> contextClass) {
        if (ProcessContext.class.equals(contextClass)) {
            String ruleflowGroupName = getActivation().getRule().getRuleFlowGroup();
            if (ruleflowGroupName != null) {
                Map<Long, String> nodeInstances = ((InternalRuleFlowGroup) workingMemory.getAgenda().getRuleFlowGroup(ruleflowGroupName)).getNodeInstances();
                if (!nodeInstances.isEmpty()) {
                    if (nodeInstances.size() > 1) {
                        // TODO
                        throw new UnsupportedOperationException(
                            "Not supporting multiple node instances for the same ruleflow group");
                    }
                    Map.Entry<Long, String> entry = nodeInstances.entrySet().iterator().next();
                    ProcessInstance processInstance = workingMemory.getProcessInstance(entry.getKey());
                    org.drools.spi.ProcessContext context = new org.drools.spi.ProcessContext(workingMemory.getKnowledgeRuntime());
                    context.setProcessInstance(processInstance);
                    String nodeInstance = entry.getValue();
                    String[] nodeInstanceIds = nodeInstance.split(":");
                    NodeInstanceContainer container = (WorkflowProcessInstance) processInstance;
                    for (int i = 0; i < nodeInstanceIds.length; i++) {
                        for (NodeInstance subNodeInstance: container.getNodeInstances()) {
                            if (subNodeInstance.getId() == new Long(nodeInstanceIds[i])) {
                                if (i == nodeInstanceIds.length - 1) {
                                    context.setNodeInstance(subNodeInstance);
                                    break;
                                } else {
                                    container = (NodeInstanceContainer) subNodeInstance;
                                }
                            }
                        }
                    }
                    return (T) context;
                }
            }
        }
        return null;
    }




    public <T, K> T don( K core, Class<T> trait, boolean logical ) {
        TraitFactory builder = new TraitFactory( this.getKnowledgeRuntime().getKnowledgeBase() );
        boolean needsUpdate = false;

        TraitableBean inner;
        if ( core instanceof TraitableBean) {
            inner = (TraitableBean) core;
        } else {
            CoreWrapper<K> wrapper = builder.getCoreWrapper( core.getClass() );
            if ( wrapper == null ) {
                throw new UnsupportedOperationException( "Error: cannot apply a trait to non-traitable class " + core.getClass() );
            }
            wrapper.init( core );
            inner = wrapper;

            needsUpdate = true;
        }


        T thing;
        if ( inner.hasTrait( trait.getName() ) ) {
            return (T) inner.getTrait( trait.getName() );
        } else {
            thing = (T) builder.getProxy( inner, trait );
        }

        if ( needsUpdate ) {
            this.update( getFactHandle( core ), inner );
        }

        if ( ! inner.hasTrait( Thing.class.getName() ) ) {
            insert( don( inner, Thing.class, false ) );
        }

        if ( logical ) {
            insertLogical( thing );
        } else {
            insert( thing );
        }

        return thing;
    }

    public <T, K> T don( Thing<K> core, Class<T> trait, boolean logical ) {
        return don( core.getCore(), trait, logical );
    }

    public <T, K> T don( K core, Class<T> trait ) {
        return don( core, trait, false );
    }

    public <T, K> T don( Thing<K> core, Class<T> trait ) {
        return don( core.getCore(), trait );
    }

    public <T,K> Thing<K> shed( Thing<K> thing, Class<T> trait ) {
        return shed((TraitableBean<K>) thing.getCore(), trait);
    }

    public <T,K> Thing<K> shed( TraitableBean<K> core, Class<T> trait ) {
        retract( core.removeTrait( trait.getName() ) );
        Thing thing = core.getTrait( Thing.class.getName() );
        update( thing );
        return thing;
    }


    public void modify(Object newObject) {
        // TODO Auto-generated method stub
        
    }

}
