package org.drools.core.beliefsystem.simple;

import org.drools.core.beliefsystem.BeliefSet;
import org.drools.core.beliefsystem.BeliefSystem;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalKnowledgeRuntime;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.LogicalDependency;
import org.drools.core.common.NamedEntryPoint;
import org.drools.core.common.TruthMaintenanceSystem;
import org.drools.core.common.WorkingMemoryAction;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.drools.core.marshalling.impl.MarshallerReaderContext;
import org.drools.core.marshalling.impl.MarshallerWriteContext;
import org.drools.core.marshalling.impl.PersisterHelper;
import org.drools.core.marshalling.impl.ProtobufMessages;
import org.drools.core.marshalling.impl.ProtobufMessages.ActionQueue.Action;
import org.drools.core.marshalling.impl.ProtobufMessages.ActionQueue.LogicalRetract;
import org.drools.core.reteoo.ObjectTypeConf;
import org.drools.core.rule.Rule;
import org.drools.core.spi.Activation;
import org.drools.core.spi.PropagationContext;
import org.drools.core.util.LinkedListEntry;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * Default implementation emulates classical Drools TMS behaviour.
 *
 */
public class ReteSimpleBeliefSystem
        implements
        BeliefSystem {
    private NamedEntryPoint        ep;
    private TruthMaintenanceSystem tms;

    public ReteSimpleBeliefSystem(NamedEntryPoint ep,
                                  TruthMaintenanceSystem tms) {
        super();
        this.ep = ep;
        this.tms = tms;
    }

    public TruthMaintenanceSystem getTruthMaintenanceSystem() {
        return this.tms;
    }

    public void insert(LogicalDependency node,
                       BeliefSet beliefSet,
                       PropagationContext context,
                       ObjectTypeConf typeConf) {
        boolean empty = beliefSet.isEmpty();

        beliefSet.add( node.getJustifierEntry() );

        if ( empty ) {
            InternalFactHandle handle = beliefSet.getFactHandle();

            ep.insert( handle,
                       handle.getObject(),
                       node.getJustifier().getRule(),
                       node.getJustifier(),
                       typeConf,
                       null );
        }
    }

    public void read(LogicalDependency node,
                     BeliefSet beliefSet,
                     PropagationContext context,
                     ObjectTypeConf typeConf) {
        //insert(node, beliefSet, context, typeConf );
        beliefSet.add( node.getJustifierEntry() );
    }

    public void delete(LogicalDependency node,
                       BeliefSet beliefSet,
                       PropagationContext context) {
        SimpleBeliefSet sBeliefSet = (SimpleBeliefSet) beliefSet;
        beliefSet.remove( node.getJustifierEntry() );

        InternalFactHandle bfh = beliefSet.getFactHandle();
        
        if ( beliefSet.isEmpty() &&
             !((context.getType() == PropagationContext.DELETION || context.getType() == PropagationContext.MODIFICATION) // retract and modifies clean up themselves
             &&
             context.getFactHandle() == bfh) ) {
            
            if ( sBeliefSet.getWorkingMemoryAction() == null ) {
                WorkingMemoryAction action = new LogicalCallback( bfh,
                                                                  context,
                                                                  node.getJustifier(),
                                                                  false,
                                                                  true );
                ep.enQueueWorkingMemoryAction( action );
                sBeliefSet.setWorkingMemoryAction( action );
            } else {
                // was previous scheduled, so make sure it's a full retract and not an update
                LogicalCallback callback = ( LogicalCallback  ) sBeliefSet.getWorkingMemoryAction();
                callback.setUpdate( false );
                callback.setFullyRetract( true );
            }
            
        } else if ( !beliefSet.isEmpty() && beliefSet.getFactHandle().getObject() == node.getObject() ) {
            // prime has changed, to update new object                      
           // Equality might have changed on the object, so remove (which uses the handle id) and add back in
           ((NamedEntryPoint)bfh.getEntryPoint()).getObjectStore().updateHandle( bfh,  ((LinkedListEntry<LogicalDependency>) beliefSet.getFirst()).getObject().getObject() );

            if ( sBeliefSet.getWorkingMemoryAction() == null ) {
                // Only schedule if we don't already have one scheduled
                WorkingMemoryAction action = new LogicalCallback( bfh,
                                                                  context,
                                                                  node.getJustifier(),
                                                                  true,
                                                                  false );
                ep.enQueueWorkingMemoryAction( action );
                sBeliefSet.setWorkingMemoryAction( action );
            }
        }
    }

    public BeliefSet newBeliefSet(InternalFactHandle fh) {
        return new SimpleBeliefSet( this, fh );
    }

    public LogicalDependency newLogicalDependency(Activation activation,
                                                  BeliefSet beliefSet,
                                                  Object object,
                                                  Object value) {
        return new SimpleLogicalDependency( activation, beliefSet, object, value );
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
                    .getTuplesCache().get( PersisterHelper.createActivationKey(_retract.getActivation().getPackageName(),
                                                                               _retract.getActivation().getRuleName(),
                                                                               _retract.getActivation().getTuple()) ).getObject();
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

        public Action serialize(MarshallerWriteContext context) {
            LogicalRetract _retract = LogicalRetract.newBuilder()
                    .setHandleId( this.handle.getId() )
                    .setActivation( PersisterHelper.createActivation( this.activation.getRule().getPackageName(),
                                                                      this.activation.getRule().getName(),
                                                                      this.activation.getTuple() ) )
                    .setFullyRetract( fullyRetract )
                    .setUpdate( update )
                    .build();
            return Action.newBuilder()
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
                    ((NamedEntryPoint) handle.getEntryPoint() ).update( handle, true, handle.getObject(), Long.MAX_VALUE, Object.class, null );
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
}
