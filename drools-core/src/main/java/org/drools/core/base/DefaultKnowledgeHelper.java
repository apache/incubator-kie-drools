/*
 * Copyright 2005 Red Hat, Inc. and/or its affiliates.
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

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.drools.core.WorkingMemory;
import org.drools.core.WorkingMemoryEntryPoint;
import org.drools.core.beliefsystem.BeliefSet;
import org.drools.core.beliefsystem.BeliefSystem;
import org.drools.core.beliefsystem.ModedAssertion;
import org.drools.core.beliefsystem.simple.SimpleLogicalDependency;
import org.drools.core.beliefsystem.simple.SimpleMode;
import org.drools.core.common.AgendaItem;
import org.drools.core.common.EqualityKey;
import org.drools.core.common.InternalAgenda;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalRuleFlowGroup;
import org.drools.core.common.InternalWorkingMemoryEntryPoint;
import org.drools.core.common.LogicalDependency;
import org.drools.core.common.NamedEntryPoint;
import org.drools.core.common.TruthMaintenanceSystemHelper;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.factmodel.traits.CoreWrapper;
import org.drools.core.factmodel.traits.Thing;
import org.drools.core.factmodel.traits.TraitableBean;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.phreak.RuleAgendaItem;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.ObjectTypeConf;
import org.drools.core.reteoo.RuleTerminalNode;
import org.drools.core.rule.Declaration;
import org.drools.core.rule.EntryPointId;
import org.drools.core.spi.Activation;
import org.drools.core.spi.KnowledgeHelper;
import org.drools.core.spi.Tuple;
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
import org.kie.api.runtime.rule.RuleUnit;
import org.kie.internal.runtime.KnowledgeRuntime;
import org.kie.internal.runtime.beliefs.Mode;

import static org.drools.core.reteoo.PropertySpecificUtil.allSetButTraitBitMask;
import static org.drools.core.reteoo.PropertySpecificUtil.onlyTraitBitSetMask;

public class DefaultKnowledgeHelper<T extends ModedAssertion<T>>
    implements
    KnowledgeHelper,
    Externalizable {

    private static final long                         serialVersionUID = 510l;

    private Activation                                activation;
    private Tuple                                     tuple;
    private WrappedStatefulKnowledgeSessionForRHS     workingMemory;

    private LinkedList<LogicalDependency<T>>          previousJustified;

    private LinkedList<LogicalDependency<SimpleMode>> previousBlocked;
    
    public DefaultKnowledgeHelper() {

    }

    public DefaultKnowledgeHelper(final WorkingMemory workingMemory) {
        this.workingMemory = new WrappedStatefulKnowledgeSessionForRHS( workingMemory );
    }

    public DefaultKnowledgeHelper(Activation activation, final WorkingMemory workingMemory) {
        this.workingMemory = new WrappedStatefulKnowledgeSessionForRHS( workingMemory );
        this.activation = activation;
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        activation = (Activation) in.readObject();
        tuple = (LeftTuple) in.readObject();
        workingMemory = (WrappedStatefulKnowledgeSessionForRHS) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject( activation );
        out.writeObject( tuple );
        out.writeObject( workingMemory );
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
            for ( dep = this.previousBlocked.getFirst(); dep != null; dep = dep.getNext() ) {
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
            InternalAgenda agenda = workingMemory.getAgenda();
            agenda.stageLeftTuple(ruleAgendaItem, targetMatch);
        }
    }

    public FactHandle insertAsync( final Object object ) {
        return this.workingMemory.insertAsync( object );
    }

    public InternalFactHandle insert(final Object object) {
        return insert( object,
                       false );
    }

    public InternalFactHandle insert(final Object object,
                                     final boolean dynamic) {
        return (InternalFactHandle) this.workingMemory.insert( object,
                                                               dynamic,
                                                               this.activation.getRule(),
                                                               this.activation );
    }

    @Override
    public InternalFactHandle insertLogical(Object object, Mode belief) {
        return insertLogical( object,
                              belief,
                              false );
    }

    @Override
    public InternalFactHandle insertLogical(Object object, Mode... beliefs) {
        return insertLogical( object,
                              beliefs,
                              false );
    }

    public InternalFactHandle insertLogical(final Object object) {
        return insertLogical( object,
                              false );
    }
    
    public InternalFactHandle insertLogical(final Object object,final boolean dynamic) {
        return insertLogical( object,
                              null,
                              dynamic );
    }    

    public InternalFactHandle insertLogical(final Object object,
                              final Object value) {
        return insertLogical( object,
                              value,
                              false );
    }
    public InternalFactHandle insertLogical(final Object object,
                                    final Object value,
                                    final boolean dynamic) {

        if ( object == null ) {
            // prevent nulls from being inserted logically
            return null;
        }

        if ( !activation.isMatched() ) {
            // Activation is already unmatched, can't do logical insertions against it
            return null;
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
            return ( (BeliefSet) dep.getJustified() ).getFactHandle();
        } else {
            // no previous matching logical dependency, so create a new one
            return workingMemory.getTruthMaintenanceSystem().insert( object,
                                                                     value,
                                                                     this.activation.getRule(),
                                                                     this.activation );
        }
    }

    public InternalFactHandle bolster( final Object object ) {
        return bolster( object, null );
    }

    public InternalFactHandle bolster( final Object object,
                                     final Object value ) {

        if ( object == null || ! activation.isMatched() ) {
            return null;
        }

        InternalFactHandle handle = getFactHandleFromWM( object );
        NamedEntryPoint ep = (NamedEntryPoint) workingMemory.getEntryPoint( EntryPointId.DEFAULT.getEntryPointId() );
        ObjectTypeConf otc = ep.getObjectTypeConfigurationRegistry().getObjectTypeConf( ep.getEntryPoint(), object );

        BeliefSystem beliefSystem;
        if ( value == null ) {
            beliefSystem = workingMemory.getTruthMaintenanceSystem().getBeliefSystem();
        } else {
            if ( value instanceof Mode ) {
                Mode m = (Mode) value;
                beliefSystem = (BeliefSystem) m.getBeliefSystem();
            } else {
                beliefSystem = workingMemory.getTruthMaintenanceSystem().getBeliefSystem();
            }
        }

        BeliefSet beliefSet = null;
        if ( handle == null ) {
            handle = workingMemory.getKnowledgeBase().getConfiguration().getComponentFactory().getFactHandleFactoryService().newFactHandle( object,
                                                                                                                                            otc,
                                                                                                                                            workingMemory, ep );
        }
        if ( handle.getEqualityKey() == null ) {
            handle.setEqualityKey( new EqualityKey( handle, EqualityKey.STATED ) );
        } else {
            beliefSet = handle.getEqualityKey().getBeliefSet();
        }
        if ( beliefSet == null ) {
            beliefSet = beliefSystem.newBeliefSet( handle );
            handle.getEqualityKey().setBeliefSet( beliefSet );
        }

        return beliefSystem.insert( beliefSystem.asMode( value ),
                                    activation.getRule(),
                                    activation,
                                    object,
                                    beliefSet,
                                    activation.getPropagationContext(),
                                    otc ).getFactHandle();
    }

    public void cancelRemainingPreviousLogicalDependencies() {
        if ( this.previousJustified != null ) {
            for ( LogicalDependency<T> dep = this.previousJustified.getFirst(); dep != null; dep = dep.getNext() ) {
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
                    workingMemory.getAgenda().stageLeftTuple(ruleAgendaItem, justified);
                }
                dep = tmp;
            }
        }        
    }
    
    public void cancelMatch(Match act) {
        AgendaItem match = ( AgendaItem ) act;
        ((RuleTerminalNode)match.getTerminalNode()).cancelMatch( match,  workingMemory);
    }

    public InternalFactHandle getFactHandle(Object object) {
        InternalFactHandle handle = getFactHandleFromWM( object );

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
    
    public InternalFactHandle getFactHandle(InternalFactHandle handle) {
        Object object = handle.getObject();
        handle = getFactHandleFromWM( object );
        if ( handle == null ) {
            throw new RuntimeException( "Update error: handle not found for object: " + object + ". Is it in the working memory?" );
        }
        return handle;
    }
    
    public void update(final FactHandle handle,
                       final Object newObject){
        InternalFactHandle h = (InternalFactHandle) handle;
        h.getEntryPoint().update( h,
                                  newObject,
                                  onlyTraitBitSetMask(),
                                  newObject.getClass(),
                                  this.activation );
    }

    public void update(final FactHandle handle) {
        update( handle, Long.MAX_VALUE );
    }

    public void update( final FactHandle handle, BitMask mask, Class<?> modifiedClass ) {
        InternalFactHandle h = (InternalFactHandle) handle;

        if (h.getDataSource() != null) {
            // This handle has been insert from a datasource, so update it
            h.getDataSource().update( h,
                                      ((InternalFactHandle)handle).getObject(),
                                      mask,
                                      modifiedClass,
                                      this.activation );
            return;
        }

        ((InternalWorkingMemoryEntryPoint) h.getEntryPoint()).update( h,
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

    public void delete(Object object, FactHandle.State fhState) {
        delete( getFactHandle( object ), fhState );
    }

    public void delete(FactHandle handle) {
        delete(handle, FactHandle.State.ALL);
    }

    public void delete(FactHandle handle, FactHandle.State fhState ) {
        Object o = ((InternalFactHandle) handle).getObject();
        if ( ((InternalFactHandle) handle).isTraiting() ) {
            delete( ((Thing) o).getCore() );
            return;
        }

        ((InternalFactHandle) handle).getEntryPoint().delete(handle,
                                                             this.activation.getRule(),
                                                             this.activation,
                                                             fhState);
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
        return this.workingMemory;
    }

    public Activation getMatch() {
        return this.activation;
    }

    public void setFocus(final String focus) {
        this.workingMemory.setFocus( focus );
    }

    public Object get(final Declaration declaration) {
        WorkingMemoryEntryPoint wmTmp = (this.tuple.get( declaration )).getEntryPoint();
        return wmTmp != null ?
               declaration.getValue( wmTmp.getInternalWorkingMemory(),
                                                     this.tuple.getObject( declaration ) )
                             : null;
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

    private InternalFactHandle getFactHandleFromWM(final Object object) {
        InternalFactHandle handle = null;
        // entry point null means it is a generated fact, not a regular inserted fact
        // NOTE: it would probably be a good idea to create a specific attribute for that
            for ( EntryPoint ep : workingMemory.getEntryPoints() ) {
                handle = (InternalFactHandle) ep.getFactHandle( object );
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

    public ClassLoader getProjectClassLoader() {
        return ((InternalKnowledgeBase)getKieRuntime().getKieBase()).getRootClassLoader();
    }

    public void run(RuleUnit ruleUnit ) {
        workingMemory.switchToRuleUnit( ruleUnit );
    }

    public void run(Class<? extends RuleUnit> ruleUnitClass) {
        workingMemory.switchToRuleUnit( ruleUnitClass );
    }

    public void guard(RuleUnit ruleUnit) {
        workingMemory.guardRuleUnit( ruleUnit, activation );
    }

    public void guard(Class<? extends RuleUnit> ruleUnitClass) {
        workingMemory.guardRuleUnit( ruleUnitClass, activation );
    }
}
