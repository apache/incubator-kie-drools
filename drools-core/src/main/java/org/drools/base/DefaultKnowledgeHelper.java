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
import org.drools.beliefsystem.BeliefSet;
import org.drools.common.AbstractRuleBase;
import org.drools.common.AgendaItem;
import org.drools.common.DefaultAgenda;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalRuleFlowGroup;
import org.drools.common.InternalWorkingMemoryActions;
import org.drools.common.InternalWorkingMemoryEntryPoint;
import org.drools.common.LogicalDependency;
import org.drools.common.ObjectTypeConfigurationRegistry;
import org.drools.common.SimpleLogicalDependency;
import org.drools.common.TruthMaintenanceSystemHelper;
import org.drools.core.util.LinkedList;
import org.drools.core.util.LinkedListEntry;
import org.drools.factmodel.traits.CoreWrapper;
import org.drools.factmodel.traits.Thing;
import org.drools.factmodel.traits.TraitFactory;
import org.drools.factmodel.traits.TraitableBean;
import org.drools.impl.KnowledgeBaseImpl;
import org.drools.reteoo.LeftTuple;
import org.drools.reteoo.ObjectTypeConf;
import org.drools.reteoo.ReteooWorkingMemory;
import org.drools.rule.Declaration;
import org.drools.rule.Rule;
import org.drools.spi.Activation;
import org.drools.spi.KnowledgeHelper;
import org.drools.spi.PropagationContext;
import org.drools.spi.Tuple;
import org.kie.event.rule.ActivationUnMatchListener;
import org.kie.runtime.Channel;
import org.kie.runtime.KieRuntime;
import org.kie.runtime.KnowledgeRuntime;
import org.kie.runtime.process.NodeInstance;
import org.kie.runtime.process.NodeInstanceContainer;
import org.kie.runtime.process.ProcessContext;
import org.kie.runtime.process.ProcessInstance;
import org.kie.runtime.process.WorkflowProcessInstance;
import org.kie.runtime.rule.Match;
import org.kie.runtime.rule.SessionEntryPoint;

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
            // the match is no longer blocked, so stage it
            ((DefaultAgenda)workingMemory.getAgenda()).getStageActivationsGroup().addActivation( targetMatch );
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
                    // the match is no longer blocked, so stage it
                    ((DefaultAgenda)workingMemory.getAgenda()).getStageActivationsGroup().addActivation( justified );
                }
                dep = tmp;
            }
        }        
    }
    
    public void cancelMatch(Match act) {
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
        InternalFactHandle h = (InternalFactHandle) handle;
        ((InternalWorkingMemoryEntryPoint) h.getEntryPoint()).update( h,
                                                                      newObject,
                                                                      Long.MAX_VALUE,
                                                                      this.activation );
        if ( getIdentityMap() != null ) {
            this.getIdentityMap().put( newObject,
                                       handle );
        }
    }

    public void update(final FactHandle handle) {
        update( handle, Long.MAX_VALUE );
    }

    public void update(final FactHandle handle, long mask) {
        InternalFactHandle h = (InternalFactHandle) handle;
        ((InternalWorkingMemoryEntryPoint) h.getEntryPoint()).update( h,
                                                                      ((InternalFactHandle)handle).getObject(),
                                                                      mask,
                                                                      this.activation );
    }

    
    public void update( Object object ) {
        update(object, Long.MAX_VALUE);
    }

    public void update(Object object, long mask) {
        update(getFactHandle(object), mask);
    }
    
    public void retract(Object object) {
       retract( getFactHandle(object) );
    }

    public void retract(final FactHandle handle) {
        ((InternalWorkingMemoryEntryPoint) ((InternalFactHandle) handle).getEntryPoint()).delete( handle,
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
        return (Declaration) ((AgendaItem)this.activation).getRuleTerminalNode().getSubRule().getOuterDeclarations().get( identifier );
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
            for ( SessionEntryPoint ep : workingMemory.getEntryPoints().values() ) {
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

        T thing = applyTrait( core, trait, logical );

        return doInsertTrait( thing, logical );

    }

    protected <T> T doInsertTrait( T thing, boolean logical ) {
        FactHandle fh = insert( thing );
        
        if ( logical ) {
            AgendaItem agendaItem = ( AgendaItem ) activation;
            
            RetractTrait newUnMatch = new RetractTrait(fh);
            ActivationUnMatchListener unmatch = agendaItem.getActivationUnMatchListener();
            if ( unmatch != null ) {
                newUnMatch.setNext( ( RetractTrait ) unmatch );
            }
            agendaItem.setActivationUnMatchListener( newUnMatch );
        }
        return thing;
    }

    public KieRuntime getKieRuntime() {
        return getKnowledgeRuntime();
    }

    public static class RetractTrait implements ActivationUnMatchListener {
        private FactHandle fh;

        private RetractTrait next;
        
        public RetractTrait(FactHandle fh) {
            this.fh = fh;
        }

        public void unMatch(org.kie.runtime.rule.Session wm,
                            Match activation) {
            wm.retract( fh );
            if ( next != null ) {
                next.unMatch( wm, activation );
            }
        }

        public FactHandle getFh() {
            return fh;
        }

        public void setFh(FactHandle fh) {
            this.fh = fh;
        }

        public RetractTrait getNext() {
            return next;
        }

        public void setNext(RetractTrait next) {
            this.next = next;
        }                
        
    }

    protected <T, K> T applyTrait( K core, Class<T> trait, boolean logical ) {
        AbstractRuleBase arb = (AbstractRuleBase) ((KnowledgeBaseImpl) this.getKnowledgeRuntime().getKieBase() ).getRuleBase();
        TraitFactory builder = arb.getConfiguration().getComponentFactory().getTraitFactory();

        boolean needsWrapping = ! ( core instanceof TraitableBean );

        TraitableBean inner = needsWrapping ? asTraitable( core, builder ) : (TraitableBean) core;
        if ( needsWrapping ) {
            InternalFactHandle h = (InternalFactHandle) getFactHandle( core );
            InternalWorkingMemoryEntryPoint ep = (InternalWorkingMemoryEntryPoint) h.getEntryPoint();
            ObjectTypeConfigurationRegistry reg = ep.getObjectTypeConfigurationRegistry();

            ObjectTypeConf coreConf = reg.getObjectTypeConf( ep.getEntryPoint(), core );

            ObjectTypeConf innerConf = reg.getObjectTypeConf( ep.getEntryPoint(), inner );
            if ( coreConf.isTMSEnabled() ) {
                innerConf.enableTMS();
            }
        }

        return processTraits( core, trait, builder, needsWrapping, inner, logical );
    }

    protected <K> TraitableBean asTraitable( K core, TraitFactory builder ) {
        CoreWrapper<K> wrapper = builder.getCoreWrapper( core.getClass() );
        if ( wrapper == null ) {
            throw new UnsupportedOperationException( "Error: cannot apply a trait to non-traitable class " + core.getClass() );
        }
        wrapper.init( core );
        return wrapper;
    }
    
    
    protected <T,K> T processTraits( K core, Class<T> trait, TraitFactory builder, boolean needsUpdate, TraitableBean inner, boolean logical ) {

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
            don( inner, Thing.class, logical );
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
