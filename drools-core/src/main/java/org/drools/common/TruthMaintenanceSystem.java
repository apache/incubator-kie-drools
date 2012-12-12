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
import org.drools.beliefsystem.BeliefSet;
import org.drools.beliefsystem.BeliefSystem;
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
import org.kie.runtime.rule.FactHandle;

/**
 * The Truth Maintenance System is responsible for tracking two things. Firstly
 * It maintains a Map to track the classes with the same Equality, using the
 * EqualityKey. The EqualityKey has an internal datastructure which references
 * all the handles which are equal. Secondly It maintains another map tracking
 * the  justificiations for logically asserted facts.
 */
public class TruthMaintenanceSystem {

    private AbstractWorkingMemory wm;

    private NamedEntryPoint       ep;

    private ObjectHashMap         equalityKeyMap;

    private BeliefSystem          beliefSystem;

    public TruthMaintenanceSystem() {}

    public TruthMaintenanceSystem(final AbstractWorkingMemory wm,
                                  NamedEntryPoint ep) {
        this.wm = wm;

        //this.justifiedMap = new ObjectHashMap();
        this.equalityKeyMap = new ObjectHashMap();
        this.equalityKeyMap.setComparator( EqualityKeyComparator.getInstance() );

        beliefSystem = wm.getSessionConfiguration().getBeliefSystemType().createInstance( ep, this );
    }

    public ObjectHashMap getEqualityKeyMap() {
        return this.equalityKeyMap;
    }

    public Object put(final EqualityKey key) {
        return this.equalityKeyMap.put( key,
                                   key,
                                   false );
    }

    public EqualityKey get(final EqualityKey key) {
        return (EqualityKey) this.equalityKeyMap.get( key );
    }

    public EqualityKey get(final Object object) {
        return (EqualityKey) this.equalityKeyMap.get( object );
    }

    public EqualityKey remove(final EqualityKey key) {
        return (EqualityKey) this.equalityKeyMap.remove( key );
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
                                      final Object object,
                                      final Object value,
                                      final Activation activation,
                                      final PropagationContext context,
                                      final Rule rule,
                                      final ObjectTypeConf typeConf) throws FactException {
        addLogicalDependency( handle, object, value, activation, context, rule, typeConf, true );
    }

    public void addLogicalDependency(final InternalFactHandle handle,
                                     final Object object,
                                     final Object value,
                                     final Activation activation,
                                     final PropagationContext context,
                                     final Rule rule,
                                     final ObjectTypeConf typeConf) throws FactException {
        addLogicalDependency( handle, object, value, activation, context, rule, typeConf, false );
    }

    public void addLogicalDependency(final InternalFactHandle handle,
                                     final Object object,
                                     final Object value,
                                     final Activation activation,
                                     final PropagationContext context,
                                     final Rule rule,
                                     final ObjectTypeConf typeConf,
                                     final boolean read) throws FactException {
        BeliefSet beliefSet = handle.getEqualityKey().getBeliefSet();
        if ( beliefSet == null ) {
            if ( context.getType() == PropagationContext.MODIFICATION ) {
                // if this was a  update, chances  are its trying  to retract a logical assertion
            }
            beliefSet = beliefSystem.newBeliefSet( handle );
            handle.getEqualityKey().setBeliefSet( beliefSet );
        }

        final LogicalDependency node = beliefSystem.newLogicalDependency( activation, beliefSet, object, value );
        activation.getRule().setHasLogicalDependency( true );

        activation.addLogicalDependency( node );

        if ( read ) {
            // used when deserialising
            beliefSystem.read( node, beliefSet, context, typeConf );
        } else {
            beliefSystem.insert( node, beliefSet, context, typeConf );
        }
    }

    public static class LogicalCallback
            implements
            WorkingMemoryAction {
        
        private InternalFactHandle     handle;
        private PropagationContext     context;
        private Activation             activation;
        
        private boolean                update;
        private boolean                fullyRetract;

        public LogicalCallback() {

        }

        public LogicalCallback(final InternalFactHandle handle,
                                      final PropagationContext context,
                                      final Activation activation,
                                      final boolean update,
                                      final boolean fullyRetract) {
            this.handle = handle;
            this.context = context;
            this.activation = activation;
            this.update = update;
            this.fullyRetract = fullyRetract;
        }

        public LogicalCallback(MarshallerReaderContext context) throws IOException {
            this.handle = context.handles.get( context.readInt() );
            this.context = context.propagationContexts.get( context.readLong() );
            this.activation = (Activation) context.terminalTupleMap.get( context.readInt() ).getObject();
        }

        public LogicalCallback(MarshallerReaderContext context,
                                      Action _action) {
            LogicalRetract _retract = _action.getLogicalRetract();

            this.handle = context.handles.get( _retract.getHandleId() );
            this.activation = (Activation) context.filter
                    .getTuplesCache().get( PersisterHelper.createActivationKey( _retract.getActivation().getPackageName(),
                                                                                _retract.getActivation().getRuleName(),
                                                                                _retract.getActivation().getTuple() ) ).getObject();
            this.context = this.activation.getPropagationContext();
            this.fullyRetract = _retract.getFullyRetract();
            this.update = _retract.getUpdate();
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
                    .setFullyRetract( fullyRetract )
                    .setUpdate( update )
                    .build();
            return ProtobufMessages.ActionQueue.Action.newBuilder()
                    .setType( ProtobufMessages.ActionQueue.ActionType.LOGICAL_RETRACT )
                    .setLogicalRetract( _retract )
                    .build();
        }

        public void readExternal(ObjectInput in) throws IOException,
                                                ClassNotFoundException {
            handle = (InternalFactHandle) in.readObject();
            context = (PropagationContext) in.readObject();
            activation = (Activation) in.readObject();
            fullyRetract = in.readBoolean();
            update =  in.readBoolean();
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeObject( handle );
            out.writeObject( context );
            out.writeObject( activation );
            out.writeBoolean( fullyRetract );
            out.writeBoolean( update );
        }
        
        public boolean isUpdate() {
            return update;
        }

        public void setUpdate(boolean update) {
            this.update = update;
        }

        public boolean isFullyRetract() {
            return fullyRetract;
        }

        public void setFullyRetract(boolean fullyRetract) {
            this.fullyRetract = fullyRetract;
        }

        public void execute(InternalWorkingMemory workingMemory) {
            NamedEntryPoint nep = (NamedEntryPoint) handle.getEntryPoint() ;
            
            BeliefSet bs = handle.getEqualityKey().getBeliefSet();
            bs.setWorkingMemoryAction( null );
            
            if ( update ) {
                if ( !bs.isEmpty() ) {
                    // We need the isEmpty check, in case the BeliefSet was made empty (due to retract) after this was scheduled
                    ((NamedEntryPoint) handle.getEntryPoint() ).update( handle, true, handle.getObject(), Long.MAX_VALUE, null );
                }
            } else  {
                if ( fullyRetract ) {
                    ((NamedEntryPoint) handle.getEntryPoint()).delete( this.handle,
                                                                        (Rule) context.getRuleOrigin(),
                                                                        this.activation );                    
                } else {                
                    final ObjectTypeConf typeConf = nep.getObjectTypeConfigurationRegistry().getObjectTypeConf( nep.getEntryPoint(),
                                                                                                                handle.getObject() );                
                    ((NamedEntryPoint) handle.getEntryPoint() ).getEntryPointNode().retractObject( handle, 
                                                                                                   context, 
                                                                                                   typeConf, 
                                                                                                   workingMemory );
                }
            }
        }

        public void execute(InternalKnowledgeRuntime kruntime) {
            execute( ((StatefulKnowledgeSessionImpl) kruntime).getInternalWorkingMemory() );
        }
    }

    public void clear() {
        this.equalityKeyMap.clear();
    }

    public BeliefSystem getBeliefSystem() {
        return beliefSystem;
    } 
}
