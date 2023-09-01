package org.drools.serialization.protobuf.actions;

import org.drools.core.rule.consequence.InternalMatch;
import org.drools.serialization.protobuf.ProtobufInputMarshaller;
import org.drools.tms.beliefsystem.simple.BeliefSystemLogicalCallback;
import org.drools.core.marshalling.MarshallerReaderContext;
import org.drools.core.marshalling.MarshallerWriteContext;
import org.drools.serialization.protobuf.PersisterHelper;
import org.drools.serialization.protobuf.ProtobufMessages;
import org.drools.serialization.protobuf.ProtobufWorkingMemoryAction;

public class ProtobufBeliefSystemLogicalCallback extends BeliefSystemLogicalCallback implements ProtobufWorkingMemoryAction {

    public ProtobufBeliefSystemLogicalCallback( MarshallerReaderContext context, ProtobufMessages.ActionQueue.Action _action) {
        ProtobufMessages.ActionQueue.LogicalRetract _retract = _action.getLogicalRetract();

        this.handle = context.getHandles().get( _retract.getHandleId() );
        this.internalMatch = (InternalMatch) ((ProtobufInputMarshaller.PBActivationsFilter)context.getFilter())
                .getTuplesCache().get( PersisterHelper.createActivationKey(_retract.getActivation().getPackageName(),
                        _retract.getActivation().getRuleName(),
                        _retract.getActivation().getTuple()) ).getContextObject();
        this.context = this.internalMatch.getPropagationContext();
        this.fullyRetract = _retract.getFullyRetract();
        this.update = _retract.getUpdate();
    }

    @Override
    public ProtobufMessages.ActionQueue.Action serialize( MarshallerWriteContext context) {
        ProtobufMessages.ActionQueue.LogicalRetract _retract = ProtobufMessages.ActionQueue.LogicalRetract.newBuilder()
                .setHandleId( this.handle.getId() )
                .setActivation( PersisterHelper.createActivation( this.internalMatch.getRule().getPackageName(),
                        this.internalMatch.getRule().getName(),
                        this.internalMatch.getTuple()))
                .setFullyRetract( fullyRetract )
                .setUpdate( update )
                .build();
        return ProtobufMessages.ActionQueue.Action.newBuilder()
                .setType( ProtobufMessages.ActionQueue.ActionType.LOGICAL_RETRACT )
                .setLogicalRetract( _retract )
                .build();
    }
}
