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

package org.drools.common;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.FactException;
import org.drools.core.util.LinkedList;
import org.drools.core.util.LinkedListEntry;
import org.drools.core.util.ObjectHashMap;
import org.drools.impl.StatefulKnowledgeSessionImpl;
import org.drools.marshalling.impl.MarshallerReaderContext;
import org.drools.marshalling.impl.MarshallerWriteContext;
import org.drools.marshalling.impl.PersisterHelper;
import org.drools.marshalling.impl.ProtobufMessages;
import org.drools.marshalling.impl.ProtobufMessages.ActionQueue.Action;
import org.drools.marshalling.impl.ProtobufMessages.ActionQueue.LogicalRetract;
import org.drools.reteoo.ObjectTypeConf;
import org.drools.rule.Rule;
import org.drools.spi.Activation;
import org.drools.spi.PropagationContext;

/**
 * The Truth Maintenance System is responsible for tracking two things. Firstly
 * It maintains a Map to track the classes with the same Equality, using the
 * EqualityKey. The EqualityKey has an internal datastructure which references
 * all the handles which are equal. Secondly It maintains another map tracking
 * the  justificiations for logically asserted facts.
 */
public class TruthMaintenanceSystem {

    private AbstractWorkingMemory        wm;

    private ObjectHashMap                justifiedMap;

    private ObjectHashMap                assertMap;

    private BeliefSystem beliefSystem;

    public TruthMaintenanceSystem() {}

    public TruthMaintenanceSystem(final AbstractWorkingMemory wm) {
        this.wm = wm;

        this.justifiedMap = new ObjectHashMap();
        this.assertMap = new ObjectHashMap();
        this.assertMap.setComparator( EqualityKeyComparator.getInstance() );
        
        beliefSystem = new SimpleBeliefSystem(wm, this);
    }

    public ObjectHashMap getJustifiedMap() {
        return this.justifiedMap;
    }

    public ObjectHashMap getAssertMap() {
        return this.assertMap;
    }

    public Object put(final EqualityKey key) {
        return this.assertMap.put( key,
                                   key,
                                   false );
    }

    public EqualityKey get(final EqualityKey key) {
        return (EqualityKey) this.assertMap.get( key );
    }

    public EqualityKey get(final Object object) {
        return (EqualityKey) this.assertMap.get( object );
    }

    public EqualityKey remove(final EqualityKey key) {
        return (EqualityKey) this.assertMap.remove( key );
    }

    /**
     * The FactHandle is being removed from the system so remove any logical dependencies
     * between the  justified FactHandle and its justifiers. Removes the FactHandle key
     * from the justifiedMap. It then iterates over all the LogicalDependency nodes, if any,
     * in the returned Set and removes the LogicalDependency node from the LinkedList maintained
     * by the Activation.
     *
     * @see LogicalDependency
     *
     * @param handle - The FactHandle to be removed
     * @throws FactException
     */
    public void removeLogicalDependencies(final InternalFactHandle handle) throws FactException {
        final BeliefSet beliefSet = (BeliefSet) this.justifiedMap.remove( handle.getId() );
        if ( beliefSet != null && !beliefSet.isEmpty() ) {
            for ( LinkedListEntry entry = (LinkedListEntry) beliefSet.getFirst(); entry != null; entry = (LinkedListEntry) entry.getNext() ) {
                final LogicalDependency node = (LogicalDependency) entry.getObject();
                node.getJustifier().getLogicalDependencies().remove( node );
            }
        }
    }    
    
    /**
     * An Activation is no longer true so it no longer justifies  any  of the logical facts
     * it logically  asserted. It iterates  over the Activation's LinkedList of DependencyNodes
     * it retrieves the justitication  set for each  DependencyNode's FactHandle and  removes
     * itself. If the Set is empty it retracts the FactHandle from the WorkingMemory.
     *
     * @param activation
     * @param context
     * @param rule
     * @throws FactException
     */
    public void removeLogicalDependencies(final Activation activation,
                                          final PropagationContext context,
                                          final Rule rule) throws FactException {
        final LinkedList list = activation.getLogicalDependencies();
        if ( list == null || list.isEmpty() ) {
            return;
        }

        for ( LogicalDependency node = (LogicalDependency) list.getFirst(); node != null; node = (LogicalDependency) node.getNext() ) {
            removeLogicalDependency( activation, node, context );
        }
    }

    public void removeLogicalDependency(final Activation activation,
                                        final LogicalDependency node,
                                        final PropagationContext context) {
        final InternalFactHandle handle = (InternalFactHandle) node.getJustified();
        final BeliefSet beliefSet = (BeliefSet) this.justifiedMap.get( handle.getId() );
        
        if ( beliefSet != null ) {
            beliefSystem.delete( node, beliefSet, context );
        }
    }    

    /**
     * Adds a justification for the FactHandle to the justifiedMap.
     *
     * @param handle
     * @param activation
     * @param context
     * @param rule
     * @param typeConf 
     * @throws FactException
     */
    public void readLogicalDependency(final InternalFactHandle handle,
                                      final Activation activation,
                                      final PropagationContext context,
                                      final Rule rule,
                                      final ObjectTypeConf typeConf) throws FactException {
        addLogicalDependency(handle, activation, context, rule, typeConf, true);
    }
    
    public void addLogicalDependency(final InternalFactHandle handle,
                                      final Activation activation,
                                      final PropagationContext context,
                                      final Rule rule,
                                      final ObjectTypeConf typeConf) throws FactException {
        addLogicalDependency(handle, activation, context, rule, typeConf, false);
    }
    
    public void addLogicalDependency(final InternalFactHandle handle,
                                     final Activation activation,
                                     final PropagationContext context,
                                     final Rule rule,
                                     final ObjectTypeConf typeConf,
                                     final boolean read) throws FactException {
        final LogicalDependency node = new LogicalDependency( activation,
                                                              handle );
        activation.getRule().setHasLogicalDependency( true );

        activation.addLogicalDependency( node );

        BeliefSet beliefSet = (BeliefSet) this.justifiedMap.get( handle.getId() );
        if ( beliefSet == null ) {
            if ( context.getType() == PropagationContext.MODIFICATION ) {
                // if this was a  update, chances  are its trying  to retract a logical assertion
            }
            beliefSet = beliefSystem.newBeliefSet();
            this.justifiedMap.put( handle.getId(),
                                   beliefSet );
        }
        if ( read ) {
            // used when deserialising
            beliefSystem.read( node, beliefSet, context, typeConf );            
        } else {
            beliefSystem.insert( node, beliefSet, context, typeConf );
        }
    }   

    public static class LogicalRetractCallback
            implements
            WorkingMemoryAction {
        private TruthMaintenanceSystem tms;
        private LogicalDependency      node;
        private BeliefSet         beliefSet;
        private InternalFactHandle     handle;
        private PropagationContext     context;
        private Activation             activation;

        public LogicalRetractCallback() {

        }

        public LogicalRetractCallback(final TruthMaintenanceSystem tms,
                                      final LogicalDependency node,
                                      final BeliefSet beliefSet,
                                      final InternalFactHandle handle,
                                      final PropagationContext context,
                                      final Activation activation) {
            this.tms = tms;
            this.node = node;
            this.beliefSet = beliefSet;
            this.handle = handle;
            this.context = context;
        }

        public LogicalRetractCallback(MarshallerReaderContext context) throws IOException {
            this.tms = context.wm.getTruthMaintenanceSystem();

            this.handle = context.handles.get( context.readInt() );
            this.context = context.propagationContexts.get( context.readLong() );
            this.activation = (Activation) context.terminalTupleMap.get( context.readInt() ).getObject();

            this.beliefSet = (BeliefSet) this.tms.getJustifiedMap().get( handle.getId() );

            for ( LinkedListEntry entry = (LinkedListEntry) beliefSet.getFirst(); entry != null; entry = (LinkedListEntry) entry.getNext() ) {
                final LogicalDependency node = (LogicalDependency) entry.getObject();
                if ( node.getJustifier() == this.activation ) {
                    this.node = node;
                    break;
                }
            }
        }

        public LogicalRetractCallback(MarshallerReaderContext context,
                                      Action _action) {
            LogicalRetract _retract = _action.getLogicalRetract();
            this.tms = context.wm.getTruthMaintenanceSystem();

            this.handle = context.handles.get( _retract.getHandleId() );
            this.activation = (Activation) context.filter
                                                  .getTuplesCache().get( PersisterHelper.createActivationKey( _retract.getActivation().getPackageName(),
                                                                                                              _retract.getActivation().getRuleName(),
                                                                                                              _retract.getActivation().getTuple() ) ).getObject();
            this.context = this.activation.getPropagationContext();

            this.beliefSet = (BeliefSet) this.tms.getJustifiedMap().get( handle.getId() );

            for ( LinkedListEntry entry = (LinkedListEntry) beliefSet.getFirst(); entry != null; entry = (LinkedListEntry) entry.getNext() ) {
                final LogicalDependency node = (LogicalDependency) entry.getObject();
                if ( node.getJustifier() == this.activation ) {
                    this.node = node;
                    break;
                }
            }
        }

        public void write(MarshallerWriteContext context) throws IOException {
            context.writeShort( WorkingMemoryAction.LogicalRetractCallback );

            context.writeInt( this.handle.getId() );
            context.writeLong( this.context.getPropagationNumber() );
            context.writeInt( context.terminalTupleMap.get( this.activation.getTuple() ) );
        }

        public ProtobufMessages.ActionQueue.Action serialize(MarshallerWriteContext context) {
            LogicalRetract _retract = ProtobufMessages.ActionQueue.LogicalRetract.newBuilder()
                    .setHandleId( this.handle.getId() )
                    .setActivation( PersisterHelper.createActivation( this.activation.getRule().getPackageName(),
                                                                      this.activation.getRule().getName(),
                                                                      this.activation.getTuple() ) )
                    .build();
            return ProtobufMessages.ActionQueue.Action.newBuilder()
                    .setType( ProtobufMessages.ActionQueue.ActionType.LOGICAL_RETRACT )
                    .setLogicalRetract( _retract )
                    .build();
        }

        public void readExternal(ObjectInput in) throws IOException,
                                                ClassNotFoundException {
            tms = (TruthMaintenanceSystem) in.readObject();
            node = (LogicalDependency) in.readObject();
            beliefSet = (BeliefSet) in.readObject();
            handle = (InternalFactHandle) in.readObject();
            context = (PropagationContext) in.readObject();
            activation = (Activation) in.readObject();
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeObject( tms );
            out.writeObject( node );
            out.writeObject( beliefSet );
            out.writeObject( handle );
            out.writeObject( context );
            out.writeObject( activation );
        }

        public void execute(InternalWorkingMemory workingMemory) {
            if ( beliefSet.isEmpty() ) {                
                // this needs to be scheduled so we don't upset the current
                // working memory operation
                workingMemory.retract( this.handle,
                                       false,
                                       true,
                                       (Rule) context.getRuleOrigin(),
                                       this.activation );
            }
        }

        public void execute(InternalKnowledgeRuntime kruntime) {
            execute( ((StatefulKnowledgeSessionImpl) kruntime).getInternalWorkingMemory() );
        }
    }


    public void clear() {
        this.justifiedMap.clear();
        this.assertMap.clear();
    }

    public BeliefSet newTMSbeliefSet() {
        return beliefSystem.newBeliefSet();
    }

}
