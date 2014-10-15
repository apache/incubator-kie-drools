package org.drools.core.beliefsystem.simple;

import org.drools.core.beliefsystem.BeliefSet;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalKnowledgeRuntime;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.NamedEntryPoint;
import org.drools.core.common.WorkingMemoryAction;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.drools.core.marshalling.impl.MarshallerReaderContext;
import org.drools.core.marshalling.impl.MarshallerWriteContext;
import org.drools.core.marshalling.impl.PersisterHelper;
import org.drools.core.marshalling.impl.ProtobufMessages;
import org.drools.core.reteoo.ObjectTypeConf;
import org.drools.core.spi.Activation;
import org.drools.core.spi.PropagationContext;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import static org.drools.core.reteoo.PropertySpecificUtil.allSetButTraitBitMask;

public class BeliefSystemLogicalCallback
    implements
    WorkingMemoryAction {

        private InternalFactHandle handle;
        private PropagationContext context;
        private Activation activation;

        private boolean                update;
        private boolean                fullyRetract;

    public BeliefSystemLogicalCallback() {

    }

    public BeliefSystemLogicalCallback(final InternalFactHandle handle,
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

    public BeliefSystemLogicalCallback(MarshallerReaderContext context) throws IOException {
        this.handle = context.handles.get( context.readInt() );
        this.context = context.propagationContexts.get( context.readLong() );
        this.activation = (Activation) context.terminalTupleMap.get( context.readInt() ).getObject();
    }

    public BeliefSystemLogicalCallback(MarshallerReaderContext context,
                                       ProtobufMessages.ActionQueue.Action _action) {
        ProtobufMessages.ActionQueue.LogicalRetract _retract = _action.getLogicalRetract();

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

    public ProtobufMessages.ActionQueue.Action serialize(MarshallerWriteContext context) {
        ProtobufMessages.ActionQueue.LogicalRetract _retract = ProtobufMessages.ActionQueue.LogicalRetract.newBuilder()
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
                ((NamedEntryPoint) handle.getEntryPoint() ).update( handle, true, handle.getObject(), allSetButTraitBitMask(), Object.class, null );
            }
        } else  {
            if ( fullyRetract ) {
                ((NamedEntryPoint) handle.getEntryPoint()).delete( this.handle,
                                                                   context.getRuleOrigin(),
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
