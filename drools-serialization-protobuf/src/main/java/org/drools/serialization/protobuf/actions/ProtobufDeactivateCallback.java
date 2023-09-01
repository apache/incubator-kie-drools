package org.drools.serialization.protobuf.actions;

import org.drools.core.common.AgendaGroupQueueImpl;
import org.drools.core.common.InternalRuleFlowGroup;
import org.drools.core.marshalling.MarshallerReaderContext;
import org.drools.core.marshalling.MarshallerWriteContext;
import org.drools.serialization.protobuf.ProtobufMessages;
import org.drools.serialization.protobuf.ProtobufWorkingMemoryAction;

public class ProtobufDeactivateCallback extends AgendaGroupQueueImpl.DeactivateCallback implements ProtobufWorkingMemoryAction {

    public ProtobufDeactivateCallback( MarshallerReaderContext context, ProtobufMessages.ActionQueue.Action _action) {
        super( ( InternalRuleFlowGroup ) context.getWorkingMemory().getAgenda().getRuleFlowGroup( _action.getDeactivateCallback().getRuleflowGroup() ) );
    }

    @Override
    public ProtobufMessages.ActionQueue.Action serialize( MarshallerWriteContext context) {
        return ProtobufMessages.ActionQueue.Action.newBuilder()
                .setType( ProtobufMessages.ActionQueue.ActionType.DEACTIVATE_CALLBACK )
                .setDeactivateCallback( ProtobufMessages.ActionQueue.DeactivateCallback.newBuilder()
                        .setRuleflowGroup( ruleFlowGroup.getName() )
                        .build() )
                .build();
    }
}
