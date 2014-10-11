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

import org.drools.core.beliefsystem.ModedAssertion;
import org.drools.core.beliefsystem.simple.SimpleMode;
import org.drools.core.factmodel.traits.TraitRegistry;
import org.drools.core.factmodel.traits.TraitTypeMap;
import org.drools.core.rule.EntryPointId;
import org.kie.api.runtime.rule.FactHandle;
import org.drools.core.WorkingMemory;
import org.drools.core.beliefsystem.BeliefSet;
import org.drools.core.beliefsystem.ModedAssertion;
import org.drools.core.beliefsystem.simple.SimpleLogicalDependency;
import org.drools.core.beliefsystem.simple.SimpleMode;
import org.drools.core.common.AgendaItem;
import org.drools.core.common.EqualityKey;
import org.drools.core.common.InternalAgenda;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalRuleFlowGroup;
import org.drools.core.common.InternalWorkingMemoryActions;
import org.drools.core.common.InternalWorkingMemoryEntryPoint;
import org.drools.core.common.LogicalDependency;
import org.drools.core.common.NamedEntryPoint;
import org.drools.core.common.ObjectStore;
import org.drools.core.common.ObjectTypeConfigurationRegistry;
import org.drools.core.common.TruthMaintenanceSystemHelper;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.factmodel.ClassDefinition;
import org.drools.core.factmodel.traits.CoreWrapper;
import org.drools.core.factmodel.traits.LogicalTypeInconsistencyException;
import org.drools.core.factmodel.traits.Thing;
import org.drools.core.factmodel.traits.TraitFactory;
import org.drools.core.factmodel.traits.TraitFieldTMS;
import org.drools.core.factmodel.traits.TraitProxy;
import org.drools.core.factmodel.traits.TraitRegistry;
import org.drools.core.factmodel.traits.TraitType;
import org.drools.core.factmodel.traits.TraitTypeMap;
import org.drools.core.factmodel.traits.TraitableBean;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.drools.core.phreak.RuleAgendaItem;
import org.drools.core.reteoo.ObjectTypeConf;
import org.drools.core.reteoo.RuleTerminalNode;
import org.drools.core.rule.Declaration;
import org.drools.core.rule.TypeDeclaration;
import org.drools.core.spi.Activation;
import org.drools.core.spi.KnowledgeHelper;
import org.drools.core.spi.Tuple;
import org.drools.core.util.HierarchyEncoder;
import org.drools.core.util.LinkedList;
import org.drools.core.util.LinkedListEntry;
import org.drools.core.util.bitmask.BitMask;
import org.kie.api.runtime.Channel;
import org.kie.api.runtime.KieRuntime;
import org.kie.api.runtime.process.NodeInstance;
import org.kie.api.runtime.process.NodeInstanceContainer;
import org.kie.api.runtime.process.ProcessContext;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkflowProcessInstance;
import org.kie.api.runtime.rule.EntryPoint;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.Match;
import org.kie.internal.runtime.KnowledgeRuntime;
import org.kie.internal.runtime.beliefs.Mode;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;

import static org.drools.core.reteoo.PropertySpecificUtil.allSetButTraitBitMask;
import static org.drools.core.reteoo.PropertySpecificUtil.onlyTraitBitSetMask;

public class DefaultKnowledgeHelper<T extends ModedAssertion<T>>
    implements
    KnowledgeHelper,
    Externalizable {

    private static final long                         serialVersionUID = 510l;

    private Activation                                activation;
    private Tuple                                     tuple;
    private InternalWorkingMemoryActions              workingMemory;

    private IdentityHashMap<Object, FactHandle>       identityMap;

    private LinkedList<LogicalDependency<T>>          previousJustified;

    private LinkedList<LogicalDependency<SimpleMode>> previousBlocked;

    public DefaultKnowledgeHelper() {

    }

    public DefaultKnowledgeHelper(final WorkingMemory workingMemory) {
        this.workingMemory = (InternalWorkingMemoryActions) workingMemory;

        this.identityMap = null;

    }

    public DefaultKnowledgeHelper(Activation activation, final WorkingMemory workingMemory) {
        this.workingMemory = (InternalWorkingMemoryActions) workingMemory;
        this.activation = activation;
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

    public LinkedList<LogicalDependency<T>> getpreviousJustified() {
        return previousJustified;
    }

    public void blockMatch(Match act) {
        AgendaItem targetMatch = ( AgendaItem ) act;
        // iterate to find previous equal logical insertion
        LogicalDependency<SimpleMode> dep = null;
        if ( this.previousBlocked != null ) {
            for ( dep = (LogicalDependency<SimpleMode> ) this.previousBlocked.getFirst(); dep != null; dep = dep.getNext() ) {
                if ( targetMatch ==  dep.getJustified() ) {
                    this.previousBlocked.remove( dep );
                    break;
                }
            }
        }

        if ( dep == null ) {
            SimpleMode mode = new SimpleMode();
            dep = new SimpleLogicalDependency( activation, targetMatch, mode );
            mode.setObject( dep );
        }
        this.activation.addBlocked(  dep );

        if ( targetMatch.getBlockers().size() == 1 && targetMatch.isQueued()  ) {
            if ( targetMatch.getRuleAgendaItem() == null ) {
                // it wasn't blocked before, but is now, so we must remove it from all groups, so it cannot be executed.
                targetMatch.remove();
            } else {
                targetMatch.getRuleAgendaItem().getRuleExecutor().removeLeftTuple(targetMatch.getTuple());
            }

            if ( targetMatch.getActivationGroupNode() != null ) {
                targetMatch.getActivationGroupNode().getActivationGroup().removeActivation( targetMatch );
            }

            if ( targetMatch.getActivationNode() != null ) {
                final InternalRuleFlowGroup ruleFlowGroup = (InternalRuleFlowGroup) targetMatch.getActivationNode().getParentContainer();
                ruleFlowGroup.remove( targetMatch );
            }
        }
    }

    public void unblockAllMatches(Match act) {
        AgendaItem targetMatch = ( AgendaItem ) act;
        boolean wasBlocked = (targetMatch.getBlockers() != null && !targetMatch.getBlockers().isEmpty() );

        for ( LinkedListEntry entry = ( LinkedListEntry ) targetMatch.getBlockers().getFirst(); entry != null;  ) {
            LinkedListEntry tmp = ( LinkedListEntry ) entry.getNext();
            LogicalDependency dep = ( LogicalDependency ) entry.getObject();
            ((AgendaItem)dep.getJustifier()).removeBlocked( dep );
            entry = tmp;
        }
        
        if ( wasBlocked ) {
            RuleAgendaItem ruleAgendaItem = targetMatch.getRuleAgendaItem();
            InternalAgenda agenda = (InternalAgenda) workingMemory.getAgenda();
            agenda.stageLeftTuple(ruleAgendaItem, targetMatch);
        }
    }

    public FactHandle insert(final Object object) {
        return insert( object,
                       false );
    }

    public FactHandle insert(final Object object,
                       final boolean dynamic) {
        FactHandle handle = this.workingMemory.insert( object,
                                                           null,
                                                           dynamic,
                                                           false,
                                                           this.activation.getRule(),
                                                           this.activation );
        if ( this.identityMap != null ) {
            this.getIdentityMap().put( object,
                                       handle );
        }
        
        return handle;
    }

    @Override
    public void insertLogical(Object object, Mode belief) {
        insertLogical( object,
                       belief,
                       false );
    }

    @Override
    public void insertLogical(Object object, Mode... beliefs) {
        insertLogical( object,
                       beliefs,
                       false );
    }

    public void insertLogical(final Object object) {
        insertLogical( object,
                       false );
    }
    
    public void insertLogical(final Object object,final boolean dynamic) {
        insertLogical( object,
                       null,
                       dynamic );
    }    

    public void insertLogical(final Object object,
                              final Object value) {
        insertLogical( object,
                       value,
                       false );
    }
    public void insertLogical(final Object object,
                              final Object value,
                              final boolean dynamic) {
        
        if ( !activation.isMatched() ) {
            // Activation is already unmatched, can't do logical insertions against it
            return;
        }
        // iterate to find previous equal logical insertion
        LogicalDependency<T> dep = null;
        if ( this.previousJustified != null ) {
            for ( dep = this.previousJustified.getFirst(); dep != null; dep = dep.getNext() ) {                
                if ( object.equals( ((BeliefSet)dep.getJustified()).getFactHandle().getObject() ) ) {
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
            FactHandle handle = workingMemory.getTruthMaintenanceSystem().insert(object,
                                                                                 value,
                                                                                 this.activation.getRule(),
                                                                                 this.activation);
//            FactHandle handle = this.workingMemory.insert( object,
//                                                           value,
//                                                           dynamic,
//                                                           true,
//                                                           this.activation.getRule(),
//                                                           this.activation );

            if ( this.identityMap != null ) {
                this.getIdentityMap().put( object,
                                           handle );
            }
        }
    }
    
    public void cancelRemainingPreviousLogicalDependencies() {
        if ( this.previousJustified != null ) {
            for ( LogicalDependency dep = (LogicalDependency) this.previousJustified.getFirst(); dep != null; dep = (LogicalDependency) dep.getNext() ) {
                TruthMaintenanceSystemHelper.removeLogicalDependency( dep, activation.getPropagationContext() );
            }
        }
        
        if ( this.previousBlocked != null ) {
            for ( LogicalDependency<SimpleMode> dep = this.previousBlocked.getFirst(); dep != null; ) {
                LogicalDependency<SimpleMode> tmp = dep.getNext();
                this.previousBlocked.remove( dep );

                AgendaItem justified = ( AgendaItem ) dep.getJustified();
                justified.getBlockers().remove( (SimpleMode) dep.getMode());
                if (justified.getBlockers().isEmpty() ) {
                    RuleAgendaItem ruleAgendaItem = justified.getRuleAgendaItem();
                    ((InternalAgenda) workingMemory.getAgenda()).stageLeftTuple(ruleAgendaItem, justified);
                }
                dep = tmp;
            }
        }        
    }
    
    public void cancelMatch(Match act) {
        AgendaItem match = ( AgendaItem ) act;
        ((RuleTerminalNode)match.getTerminalNode()).cancelMatch( match,  workingMemory);
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
            if ( object instanceof CoreWrapper ) {
                handle = getFactHandleFromWM( ((CoreWrapper) object).getCore() );
            }
            if ( handle == null ) {
                throw new RuntimeException( "Update error: handle not found for object: " + object + ". Is it in the working memory?" );
            }
        }
        return handle;
    }
    
    public FactHandle getFactHandle(FactHandle handle) {
        Object object = ((InternalFactHandle)handle).getObject();
        handle = getFactHandleFromWM( object );
        if ( handle == null ) {
            throw new RuntimeException( "Update error: handle not found for object: " + object + ". Is it in the working memory?" );
        }
        return handle;
    }
    
    public void update(final FactHandle handle,
                       final Object newObject){
        InternalFactHandle h = (InternalFactHandle) handle;
        ((InternalWorkingMemoryEntryPoint) h.getEntryPoint()).update( h,
                                                                      newObject,
                                                                      onlyTraitBitSetMask(),
                                                                      newObject.getClass(),
                                                                      this.activation );
        if ( getIdentityMap() != null ) {
            this.getIdentityMap().put( newObject,
                                       handle );
        }
    }

    public void update(final FactHandle handle) {
        update( handle, Long.MAX_VALUE );
    }

    public void update( final FactHandle handle, BitMask mask, Class<?> modifiedClass ) {
        InternalFactHandle h = (InternalFactHandle) handle;
        ((NamedEntryPoint) h.getEntryPoint()).update( h,
                                                      ((InternalFactHandle)handle).getObject(),
                                                      mask,
                                                      modifiedClass,
                                                      this.activation );
        if ( h.isTraitOrTraitable() ) {
            workingMemory.updateTraits( h, mask, modifiedClass, this.activation );
        }
    }

    public void update( Object object ) {
        update(object, allSetButTraitBitMask(), Object.class);
    }

    public void update(Object object, BitMask mask, Class<?> modifiedClass) {
        update(getFactHandle(object), mask, modifiedClass);
    }

    public void retract(Object object) {
        delete( getFactHandle( object ) );
    }

    public void retract(final FactHandle handle) {
        delete( handle );
    }

    public void delete(Object object) {
        delete( getFactHandle( object ) );
    }

    public void delete(FactHandle handle) {
        Object o = ((InternalFactHandle) handle).getObject();
        EqualityKey key = workingMemory.getTruthMaintenanceSystem().get( o );

        if ( key == null || key.getStatus() == EqualityKey.STATED ) {
            ((InternalWorkingMemoryEntryPoint) ((InternalFactHandle) handle).getEntryPoint()).delete(handle,
                                                                                                     this.activation.getRule(),
                                                                                                     this.activation);
        }

        // after removing the stated, it could still be justified
        if ( key != null && key.getStatus() == EqualityKey.JUSTIFIED ) {
            InternalFactHandle ifh = key.getLogicalFactHandle();
            ((InternalWorkingMemoryEntryPoint) ifh.getEntryPoint()).getTruthMaintenanceSystem().delete( ifh );
        }

        if ( this.identityMap != null ) {
            this.getIdentityMap().remove( o );
        }
    }

    public RuleImpl getRule() {
        return this.activation.getRule();
    }

    public Tuple getTuple() {
        return this.tuple;
    }

    public WorkingMemory getWorkingMemory() {
        return this.workingMemory;
    }

    public KnowledgeRuntime getKnowledgeRuntime() {
        return (StatefulKnowledgeSessionImpl) this.workingMemory;
    }

    public Activation getMatch() {
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
        return ((AgendaItem)this.activation).getTerminalNode().getSubRule().getOuterDeclarations().get( identifier );
    }

    public void halt() {
        this.workingMemory.halt();
    }

    public EntryPoint getEntryPoint(String id) {
        return this.workingMemory.getEntryPoint(id);
    }

    public Channel getChannel(String id) {
        return this.workingMemory.getChannels().get(id);
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
            for ( EntryPoint ep : workingMemory.getEntryPoints() ) {
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
            String ruleflowGroupName = getMatch().getRule().getRuleFlowGroup();
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
                    org.drools.core.spi.ProcessContext context = new org.drools.core.spi.ProcessContext(workingMemory.getKnowledgeRuntime());
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

    public void modify(Object newObject) {
        // TODO Auto-generated method stub

    }

    public KieRuntime getKieRuntime() {
        return getKnowledgeRuntime();
    }


    /* Trait helper methods */

    public <T, K> T don( Thing<K> core, Class<T> trait, boolean logical ) {
        return don( core, trait, logical, null );
    }

    public <T, K> T don( Thing<K> core, Class<T> trait, Mode... modes ) {
        return don( core, trait, true, modes );
    }

    public <T, K> T don( Thing<K> core, Class<T> trait, boolean logical, Mode... modes ) {
        return don( core.getCore(), trait, logical, modes );
    }

    public <T, K> T don( K core, Class<T> trait ) {
        return don( core, trait, false );
    }

    public <T, K> T don( Thing<K> core, Class<T> trait ) {
        return don( core.getCore(), trait );
    }

    public <T, K> T don( K core, Collection<Class<? extends Thing>> traits ) {
        return don( core, traits, false );
    }

    public <T,K> Thing<K> shed( Thing<K> thing, Class<T> trait ) {
        return shed( (TraitableBean<K, ? extends TraitableBean>) thing.getCore(), trait );
    }

    public <T, K> T don( K core, Collection<Class<? extends Thing>> traits, Mode... modes ) {
        return don( core, traits, true, modes );
    }

    public <T, K> T don( K core, Collection<Class<? extends Thing>> traits, boolean logical ) {
        return don( core, traits, logical, null );
    }

    public <T, K> T don( K core, Class<T> trait, boolean logical ) {
        return don( core, trait, logical, null );
    }

    public <T, K> T don( K core, Class<T> trait, Mode... modes ) {
        return don( core, trait, true, modes );
    }

    @Override
    public <T, K, X extends TraitableBean> Thing<K> shed( TraitableBean<K, X> core, Class<T> trait ) {
        return workingMemory.shed( this.activation, core, trait );
    }

    private <T, K> T don( K core, Collection<Class<? extends Thing>> traits, boolean b, Mode... modes ) {
        return workingMemory.don( this.activation, core, traits, b, modes );
    }

    private <T, K> T don( K core, Class<T> trait, boolean b, Mode... modes ) {
        return workingMemory.don( this.activation, core, trait, b, modes );
    }


}
