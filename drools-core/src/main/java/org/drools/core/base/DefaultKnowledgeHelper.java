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

import org.drools.core.FactException;
import org.drools.core.FactHandle;
import org.drools.core.WorkingMemory;
import org.drools.core.beliefsystem.BeliefSet;
import org.drools.core.beliefsystem.simple.SimpleLogicalDependency;
import org.drools.core.common.AbstractWorkingMemory;
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
import org.drools.core.factmodel.ClassDefinition;
import org.drools.core.factmodel.MapCore;
import org.drools.core.factmodel.traits.CoreWrapper;
import org.drools.core.factmodel.traits.Key;
import org.drools.core.factmodel.traits.LogicalMapCore;
import org.drools.core.factmodel.traits.LogicalTypeInconsistencyException;
import org.drools.core.factmodel.traits.Thing;
import org.drools.core.factmodel.traits.TraitFactory;
import org.drools.core.factmodel.traits.TraitFieldTMS;
import org.drools.core.factmodel.traits.TraitProxy;
import org.drools.core.factmodel.traits.TraitType;
import org.drools.core.factmodel.traits.TraitableBean;
import org.drools.core.phreak.RuleAgendaItem;
import org.drools.core.reteoo.ObjectTypeConf;
import org.drools.core.reteoo.ReteooRuleBase;
import org.drools.core.reteoo.RuleTerminalNode;
import org.drools.core.rule.Declaration;
import org.drools.core.rule.Package;
import org.drools.core.rule.Rule;
import org.drools.core.rule.TypeDeclaration;
import org.drools.core.spi.Activation;
import org.drools.core.spi.KnowledgeHelper;
import org.drools.core.spi.Tuple;
import org.drools.core.util.HierarchyEncoder;
import org.drools.core.util.LinkedList;
import org.drools.core.util.LinkedListEntry;
import org.kie.api.runtime.Channel;
import org.kie.api.runtime.KieRuntime;
import org.kie.api.runtime.process.NodeInstance;
import org.kie.api.runtime.process.NodeInstanceContainer;
import org.kie.api.runtime.process.ProcessContext;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkflowProcessInstance;
import org.kie.api.runtime.rule.EntryPoint;
import org.kie.api.runtime.rule.Match;
import org.kie.internal.runtime.KnowledgeRuntime;

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

public class DefaultKnowledgeHelper
    implements
    KnowledgeHelper,
    Externalizable {

    private static final long                   serialVersionUID = 510l;

    private Activation                          activation;
    private Tuple                               tuple;
    private InternalWorkingMemoryActions        workingMemory;

    private IdentityHashMap<Object, FactHandle> identityMap;

    private LinkedList<LogicalDependency>       previousJustified;
    
    private LinkedList<LogicalDependency>       previousBlocked;

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
      
    public LinkedList<LogicalDependency> getpreviousJustified() {
        return previousJustified;
    }
    
    public void blockMatch(Match act) {
        AgendaItem targetMatch = ( AgendaItem ) act;
        // iterate to find previous equal logical insertion
        LogicalDependency dep = null;
        if ( this.previousJustified != null ) {
            for ( dep = this.previousJustified.getFirst(); dep != null; dep = dep.getNext() ) {
                if ( targetMatch ==  dep.getJustified() ) {
                    this.previousJustified.remove( dep );
                    break;
                }
            }
        }
        
        if ( dep == null ) {
            dep = new SimpleLogicalDependency( activation, targetMatch );
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
                       final boolean dynamic) throws FactException {
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
        LogicalDependency dep = null;
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
            FactHandle handle = this.workingMemory.insert( object,
                                                           value,
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
                TruthMaintenanceSystemHelper.removeLogicalDependency( dep, activation.getPropagationContext() );
            }
        }
        
        if ( this.previousBlocked != null ) {
            for ( LogicalDependency dep = this.previousBlocked.getFirst(); dep != null; ) {
                LogicalDependency tmp = dep.getNext();
                this.previousBlocked.remove( dep );

                AgendaItem justified = ( AgendaItem ) dep.getJustified();
                justified.getBlockers().remove( dep.getJustifierEntry() );
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

    public FactHandle lookupFactHandle(Object object) {
        FactHandle handle = null;
        if ( identityMap != null ) {
            handle = identityMap.get( object );
        }

        if ( handle != null ) {
            return handle;
        }

        handle = getFactHandleFromWM( object );
        return handle;
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
        InternalFactHandle h = (InternalFactHandle) handle;
        ((InternalWorkingMemoryEntryPoint) h.getEntryPoint()).update( h,
                                                                      newObject,
                                                                      Long.MIN_VALUE,
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

    public void update( final FactHandle handle, long mask, Class<?> modifiedClass ) {
        InternalFactHandle h = (InternalFactHandle) handle;
        ((NamedEntryPoint) h.getEntryPoint()).update( h,
                                                      h.getEqualityKey() != null && h.getEqualityKey().getStatus() == EqualityKey.JUSTIFIED,
                                                      ((InternalFactHandle)handle).getObject(),
                                                      mask,
                                                      modifiedClass,
                                                      this.activation );
        if ( h.isTraitOrTraitable() ) {
            if (  h.isTraitable() ) {
                // this is a traitable core object, so its traits must be updated as well
                if ( ((TraitableBean) h.getObject()).hasTraits() ) {
                    updateTraits( h.getObject(), mask, null, modifiedClass, null, ((TraitableBean) h.getObject()).getMostSpecificTraits()  );
                }
            } else if ( h.isTraiting() ) {
                Thing x = (Thing) h.getObject();
                // in case this is a proxy
                if ( x != x.getCore() ) {
                    Object core = x.getCore();
                    InternalFactHandle coreHandle = (InternalFactHandle) getFactHandle( core );
                    ((NamedEntryPoint) coreHandle.getEntryPoint()).update(
                            coreHandle,
                            coreHandle.getEqualityKey() != null && coreHandle.getEqualityKey().getStatus() == EqualityKey.JUSTIFIED,
                            core,
                            mask,
                            modifiedClass,
                            this.activation );
                    BitSet veto = ((TraitProxy) x).getTypeCode();
                    updateTraits( core, mask, x, modifiedClass, veto, ((TraitableBean) core).getMostSpecificTraits()  );
                }
            }
        }
    }


    private void updateTraits( Object object, long mask, Thing originator, Class<?> modifiedClass, BitSet veto, Collection<Thing> mostSpecificTraits ) {
        updateManyTraits( object, mask, Arrays.asList( originator ), modifiedClass, veto, mostSpecificTraits );
    }

    private void updateManyTraits( Object object, long mask, Collection<Thing> originators, Class<?> modifiedClass, BitSet veto, Collection<Thing> mostSpecificTraits ) {
        veto = veto != null ? (BitSet) veto.clone() : null;

        for ( Thing t : mostSpecificTraits ) {
            if ( ! originators.contains( t ) ) {
                TraitProxy proxy = (TraitProxy) t;

                proxy.setTypeFilter( veto );
                InternalFactHandle h = (InternalFactHandle) lookupFactHandle( t );
                if ( h != null ) {
                    ((NamedEntryPoint) h.getEntryPoint()).update( h,
                            h.getEqualityKey() != null && h.getEqualityKey().getStatus() == EqualityKey.JUSTIFIED,
                            t,
                            mask,
                            modifiedClass,
                            this.activation );
                }
                proxy.setTypeFilter( null );

                BitSet tc = proxy.getTypeCode();
                if ( veto == null ) {
                    veto = (BitSet) tc.clone();
                } else {
                    veto.or( tc );
                }
            }
        }
    }


    public void update( Object object ) {
        update(object, Long.MAX_VALUE, Object.class);
    }

    public void update(Object object, long mask, Class<?> modifiedClass) {
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

    public void delete(final FactHandle handle) {
        Object o = ((InternalFactHandle) handle).getObject();
        ((InternalWorkingMemoryEntryPoint) ((InternalFactHandle) handle).getEntryPoint()).delete( handle,
                                                                                                  this.activation.getRule(),
                                                                                                  this.activation );
        if ( this.identityMap != null ) {
            this.getIdentityMap().remove( o );
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
        return ((AbstractWorkingMemory) this.workingMemory).getKnowledgeRuntime();
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
        return (Declaration) ((AgendaItem)this.activation).getTerminalNode().getSubRule().getOuterDeclarations().get( identifier );
    }

    public void halt() {
        this.workingMemory.halt();
    }

    public EntryPoint getEntryPoint(String id) {
        return this.workingMemory.getEntryPoints().get( id );
    }

    public Channel getChannel(String id) {
        return this.workingMemory.getChannels().get( id );
    }

    public Map<String, EntryPoint> getEntryPoints() {
        return Collections.unmodifiableMap( this.workingMemory.getEntryPoints() );
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
            for ( EntryPoint ep : workingMemory.getEntryPoints().values() ) {
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



    public <T, K> T don( K core, Collection<Class<? extends Thing>> traits ) {
        return don( core, traits, false );
    }

    public <T, K> T don( K core, Collection<Class<? extends Thing>> traits, boolean logical ) {
        if ( core instanceof Thing && ( (Thing) core ).getCore() != core ) {
            return don( ((Thing) core).getCore(), traits, logical );
        }
        if ( traits.isEmpty() ) {
            return (T) don( core, Thing.class, logical );
        }
        try {
            T thing = applyManyTraits( core, traits, null, logical );
            return thing;
        } catch ( LogicalTypeInconsistencyException ltie ) {
            ltie.printStackTrace();
            return null;
        }
    }

    public <T, K> T don( K core, Class<T> trait, boolean logical ) {
        if ( core instanceof Thing && ( (Thing) core ).getCore() != core ) {
            return don( ((Thing) core).getCore(), trait, logical );
        }
        try {
            T thing = applyTrait( core, trait, null, logical );
            return thing;
        } catch ( LogicalTypeInconsistencyException ltie ) {
            ltie.printStackTrace();
            return null;
        }
    }

    protected <T> T doInsertTrait( T thing, Object core, boolean logical, BitSet boundary ) {
        if ( thing == core ) {
            return thing;
        }

        ((TraitProxy) thing).setTypeFilter( boundary );
        if ( logical ) {
            insertLogical( thing );
        } else {
            insert( thing );
        }
        ((TraitProxy) thing).setTypeFilter( null );
        return thing;
    }

	public KieRuntime getKieRuntime() {
        return getKnowledgeRuntime();
    }

    protected <K> TraitableBean<K,CoreWrapper<K>> asTraitable( K core, TraitFactory builder ) {
        ClassDefinition coreDef = lookupClassDefinition( core );

        if ( core instanceof Map ) {
            if ( ! coreDef.isTraitable() ) {
                throw new UnsupportedOperationException( "Error: cannot apply a trait to non-traitable class " + core.getClass() + ". Was it declared as @Traitable? ");
            }
            return coreDef.isFullTraiting() ? new LogicalMapCore( (Map) core ) : new MapCore( (Map) core );
        }

        CoreWrapper<K> wrapper = builder.getCoreWrapper( core.getClass(), coreDef );
        if ( wrapper == null ) {
            throw new UnsupportedOperationException( "Error: cannot apply a trait to non-traitable class " + core.getClass() + ". Was it declared as @Traitable? ");
        }
        wrapper.init( core );
        return wrapper;
    }

    protected  <K> ClassDefinition lookupClassDefinition( K core ) {
        Package pack = this.getWorkingMemory().getRuleBase().getPackage( core.getClass().getPackage().getName() );
        if ( pack != null ) {
            TypeDeclaration decl = pack.getTypeDeclaration( core.getClass() );
            if ( decl != null ) {
                return decl.getTypeClassDef();
            }
        }
        return null;
    }



    protected <T, K> T applyManyTraits( K core, Collection<Class<? extends Thing>> traits, Object value, boolean logical) throws LogicalTypeInconsistencyException {
        // Precondition : traits is not empty, checked by don

        TraitFactory builder = TraitFactory.getTraitBuilderForKnowledgeBase( this.getKnowledgeRuntime().getKieBase() );

        TraitableBean inner = makeTraitable( core, builder, logical );

        Collection<Thing> mostSpecificTraits = inner.getMostSpecificTraits();
        boolean newTraitsAdded = false;
        T firstThing = null;
        Map<Thing, BitSet> things = new HashMap<Thing, BitSet>( traits.size() );

        for ( Class<?> trait : traits ) {
            boolean needsProxy = trait.isAssignableFrom( inner.getClass() );
            boolean hasTrait = inner.hasTrait( trait.getName() );
            boolean needsUpdate = needsProxy || core != inner;

            if ( ! hasTrait ) {
                BitSet boundary = inner.getCurrentTypeCode() != null ? (BitSet) inner.getCurrentTypeCode().clone() : null;

                T thing = (T) asTrait( core, inner, trait, needsProxy, hasTrait, needsUpdate, builder, logical );

                configureTrait( thing, value );

                things.put( (Thing) thing, boundary );

                if ( ! newTraitsAdded && trait != Thing.class ) {
                    firstThing = thing;
                    newTraitsAdded = true;
                }
            }
        }

        for ( Thing t : things.keySet() ) {
            doInsertTrait( t, core, logical, things.get( t ) );
        }

        if ( newTraitsAdded ) {
            if ( mostSpecificTraits != null ) {
                updateCore( inner, core, null, logical );
                if ( ! mostSpecificTraits.isEmpty() ) {
                    updateManyTraits( inner, Long.MIN_VALUE, things.keySet(), core.getClass(), null, mostSpecificTraits );
                }
            }
        }
        return firstThing;
    }



    protected <T, K> T applyTrait( K core, Class<T> trait, Object value, boolean logical ) throws LogicalTypeInconsistencyException {
        TraitFactory builder = TraitFactory.getTraitBuilderForKnowledgeBase( this.getKnowledgeRuntime().getKieBase() );

        TraitableBean inner = makeTraitable( core, builder, logical );

        boolean needsProxy = trait.isAssignableFrom( inner.getClass() );
        boolean hasTrait = inner.hasTrait( trait.getName() );
        boolean needsUpdate = needsProxy || core != inner;

        BitSet boundary = inner.getCurrentTypeCode() != null ? (BitSet) inner.getCurrentTypeCode().clone() : null;

        Collection<Thing> mostSpecificTraits = getTraitBoundary( inner, needsProxy, hasTrait, trait );

        T thing = asTrait( core, inner, trait, needsProxy, hasTrait, needsUpdate, builder, logical );

        configureTrait( thing, value );

        thing = doInsertTrait( thing, core, logical, boundary );

        refresh( thing, core, inner, trait, mostSpecificTraits, logical );

        if ( trait != Thing.class && inner._getFieldTMS() != null ) {
            inner._getFieldTMS().resetModificationMask();
        }
        return thing;
    }

    protected Collection<Thing> getTraitBoundary( TraitableBean inner, boolean needsProxy, boolean hasTrait, Class trait ) {
        boolean refresh = ! needsProxy && ! hasTrait && Thing.class != trait;
        if ( refresh ) {
            return inner.getMostSpecificTraits();
        }
        return null;
    }

    private <T,K> void refresh( T thing, K core, TraitableBean inner, Class<T> trait, Collection<Thing> mostSpecificTraits, boolean logical ) {
        if ( mostSpecificTraits != null ) {
            updateCore( inner, core, trait, logical );
            if ( ! mostSpecificTraits.isEmpty() ) {
                updateTraits( inner, Long.MIN_VALUE, (Thing) thing, trait, null, mostSpecificTraits );
            }
        } else if ( Thing.class == trait ) {
            updateCore( inner, core, trait, logical );
        }
    }

    private <T> void updateCore( TraitableBean inner, Object core, Class<T> trait, boolean logical ) {
        FactHandle handle = lookupFactHandle( inner );
        InternalFactHandle h = (InternalFactHandle) handle;
        if ( handle != null ) {
            TraitFieldTMS fieldTMS = inner._getFieldTMS();
            long mask = fieldTMS == null ? Long.MIN_VALUE : fieldTMS.getModificationMask();
            ((NamedEntryPoint) h.getEntryPoint()).update( h,
                                                          h.getEqualityKey() != null && h.getEqualityKey().getStatus() == EqualityKey.JUSTIFIED,
                                                          ((InternalFactHandle)handle).getObject(),
                                                          mask,
                                                          core.getClass(),
                                                          this.activation );
        } else {
            handle = this.workingMemory.insert( inner,
                                                null,
                                                false,
                                                logical,
                                                this.activation.getRule(),
                                                this.activation );
            if ( this.identityMap != null ) {
                this.getIdentityMap().put( inner,
                                           handle );
            }
        }

    }

    private <T, K> T asTrait( K core, TraitableBean inner, Class<T> trait, boolean needsProxy, boolean hasTrait, boolean needsUpdate, TraitFactory builder, boolean logical ) throws LogicalTypeInconsistencyException {
        T thing;
        if ( needsProxy ) {
            thing = (T) inner;
            inner.addTrait( trait.getName(), (Thing<K>) core );
        } else if ( hasTrait ) {
            thing = (T) inner.getTrait( trait.getName() );
        } else {
            thing = (T) builder.getProxy( inner, trait, logical );
        }

        if ( needsUpdate ) {
            InternalFactHandle h = (InternalFactHandle) lookupFactHandle( core );
            if ( h == null ) {
                h = lookupHandleForWrapper( core );
            }
            if ( h == null ) {
                h = (InternalFactHandle) this.workingMemory.insert( core,
                                                                    null,
                                                                    false,
                                                                    logical,
                                                                    this.activation.getRule(),
                                                                    this.activation );
                if ( this.identityMap != null ) {
                    this.getIdentityMap().put( core,
                                               h );
                }
            }
            if ( ! h.isTraitOrTraitable() ) {
                throw new IllegalStateException( "A traited working memory element is being used with a default fact handle. " +
                                                 "Please verify that its class was declared as @Traitable : " + core.getClass().getName() );
            }
            this.update( h, inner );
        }

        return thing;
    }

    private <K> InternalFactHandle lookupHandleForWrapper( K core ) {
        for ( EntryPoint ep : workingMemory.getEntryPoints().values() ) {
            ObjectStore store = ((InternalWorkingMemoryEntryPoint) ep).getObjectStore();
            Iterator iter = store.iterateFactHandles();
            while ( iter.hasNext() ) {
                InternalFactHandle handle = (InternalFactHandle) iter.next();
                if ( handle.isTraitable() && handle.getObject() instanceof CoreWrapper && ( (CoreWrapper) handle.getObject() ).getCore() == core ) {
                    return handle;
                }
            }
        }
        return null;
    }

    private <K> TraitableBean makeTraitable( K core, TraitFactory builder, boolean logical ) {
        boolean needsWrapping = ! ( core instanceof TraitableBean );

        TraitableBean<K,? extends TraitableBean> inner = needsWrapping ? asTraitable( core, builder ) : (TraitableBean<K,? extends TraitableBean>) core;
        if ( needsWrapping ) {
            InternalFactHandle h = (InternalFactHandle) lookupFactHandle( core );
            InternalWorkingMemoryEntryPoint ep = h != null ? (InternalWorkingMemoryEntryPoint) h.getEntryPoint() : (InternalWorkingMemoryEntryPoint) workingMemory.getEntryPoints().get( "DEFAULT" );
            ObjectTypeConfigurationRegistry reg = ep.getObjectTypeConfigurationRegistry();

            ObjectTypeConf coreConf = reg.getObjectTypeConf( ep.getEntryPoint(), core );

            ObjectTypeConf innerConf = reg.getObjectTypeConf( ep.getEntryPoint(), inner );
            if ( coreConf.isTMSEnabled() ) {
                innerConf.enableTMS();
            }
            if ( inner._getFieldTMS() != null && inner._getFieldTMS().needsInit() ) {
                inner._getFieldTMS().init( workingMemory );
            }
        } else {
            TraitFieldTMS ftms = inner._getFieldTMS();
            if ( ftms != null ) {
                FactHandle handle = lookupFactHandle( inner );
                if ( handle == null ) {
                    handle = this.workingMemory.insert( inner,
                                                        null,
                                                        false,
                                                        logical,
                                                        this.activation.getRule(),
                                                        this.activation );
                    if ( this.identityMap != null ) {
                        this.getIdentityMap().put( inner,
                                                   handle );
                    }
                }
                if ( ftms.needsInit() ) {
                    ftms.init( workingMemory );
                }
            }
        }
        return inner;
    }

    protected <T> void configureTrait( T thing, Object value ) {

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
        return shed( (TraitableBean<K,? extends TraitableBean>) thing.getCore(), trait );
    }

    public <T,K,X extends TraitableBean> Thing<K> shed( TraitableBean<K,X> core, Class<T> trait ) {
        if ( trait.isAssignableFrom( core.getClass() ) ) {
            Collection removedTraits = core.removeTrait( trait.getName() );
            if ( ! removedTraits.isEmpty() ) {
                update( core, Long.MIN_VALUE, core.getClass() );
                //updateTraits( core, Long.MIN_VALUE, null, core.getClass(), null, ((TraitableBean) core).getMostSpecificTraits()  );
            }
            return (Thing<K>) core;
        } else {
            Collection<Thing<K>> removedTypes;
            Thing<K> thing = core.getTrait( Thing.class.getName() );
            if ( trait == Thing.class ) {

                removedTypes = new ArrayList<Thing<K>>( core._getTraitMap().values() );
                for ( Thing t : removedTypes ) {
                    if ( ! ((TraitType) t).isVirtual() ) {
                        retract( t );
                    }
                }

                core._getTraitMap().clear();
                core._setTraitMap( null );
                return thing;
            } else if ( core.hasTrait( trait.getName() ) ) {
                removedTypes = core.removeTrait( trait.getName() );
            } else {
                HierarchyEncoder hier = ((ReteooRuleBase) this.workingMemory.getRuleBase()).getConfiguration().getComponentFactory().getTraitRegistry().getHierarchy();
                BitSet code = hier.getCode( trait.getName() );
                removedTypes = core.removeTrait( code );
            }

            removedTypes = new ArrayList<Thing<K>>( removedTypes );
            for ( Thing t : removedTypes ) {
                if ( ! ((TraitType) t).isVirtual() ) {
                    retract( t );
                }
            }

            if ( ! core.hasTraits() ) {
                don( core, Thing.class );
            } else if ( ! removedTypes.isEmpty() ) {
                update( core, Long.MIN_VALUE, core.getClass() );
                //updateTraits( core, Long.MIN_VALUE, null, core.getClass(), null, ((TraitableBean) core).getMostSpecificTraits()  );
            }
            return thing;
        }
    }

    public void modify(Object newObject) {
        // TODO Auto-generated method stub

    }

}
