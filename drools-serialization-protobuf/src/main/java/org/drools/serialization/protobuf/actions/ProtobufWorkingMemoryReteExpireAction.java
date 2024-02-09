/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
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