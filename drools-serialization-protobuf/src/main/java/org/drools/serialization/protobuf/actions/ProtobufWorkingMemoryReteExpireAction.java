/*
 * Copyright (c) 2020. Red Hat, Inc. and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.serialization.protobuf.actions;

import org.drools.core.common.EventFactHandle;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.drools.core.marshalling.impl.MarshallerReaderContext;
import org.drools.core.marshalling.impl.MarshallerWriteContext;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.serialization.protobuf.ProtobufMessages;
import org.drools.serialization.protobuf.ProtobufWorkingMemoryAction;

public class ProtobufWorkingMemoryReteExpireAction
        extends StatefulKnowledgeSessionImpl.WorkingMemoryReteExpireAction
        implements ProtobufWorkingMemoryAction {

    public ProtobufWorkingMemoryReteExpireAction( MarshallerReaderContext context,
                                                  ProtobufMessages.ActionQueue.Action _action) {
        this.factHandle = (EventFactHandle)context.getHandles().get(_action.getExpire().getHandleId());
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