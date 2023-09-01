package org.drools.serialization.protobuf.actions;

import org.drools.core.common.DefaultEventHandle;
import org.drools.core.impl.WorkingMemoryReteExpireAction;
import org.drools.core.marshalling.MarshallerReaderContext;
import org.drools.core.marshalling.MarshallerWriteContext;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.serialization.protobuf.ProtobufMessages;
import org.drools.serialization.protobuf.ProtobufWorkingMemoryAction;

public class ProtobufWorkingMemoryReteExpireAction
        extends WorkingMemoryReteExpireAction
        implements ProtobufWorkingMemoryAction {

    public ProtobufWorkingMemoryReteExpireAction( MarshallerReaderContext context,
                                                  ProtobufMessages.ActionQueue.Action _action) {
        this.factHandle = (DefaultEventHandle)context.getHandles().get(_action.getExpire().getHandleId());
        if (_action.getExpire().getNodeId() > 0) {
            this.node = (ObjectTypeNode) context.getSinks().get(_action.getExpire().getNodeId());
        }
    }

    @Override
    public ProtobufMessages.ActionQueue.Action serialize( MarshallerWriteContext context) {
        return ProtobufMessages.ActionQueue.Action.newBuilder()
                .setType(ProtobufMessages.ActionQueue.ActionType.EXPIRE)
                .setExpire(ProtobufMessages.ActionQueue.Expire.newBuilder()
                        .setHandleId(this.factHandle.getId())
                        .setNodeId(this.node != null ? this.node.getId() : -1)
                        .build())
                .build();
    }
}