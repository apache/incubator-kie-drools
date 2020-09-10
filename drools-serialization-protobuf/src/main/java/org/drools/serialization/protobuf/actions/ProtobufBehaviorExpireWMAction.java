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

import org.drools.core.marshalling.impl.MarshallerReaderContext;
import org.drools.core.marshalling.impl.MarshallerWriteContext;
import org.drools.core.reteoo.WindowNode;
import org.drools.core.rule.Behavior;
import org.drools.core.rule.SlidingTimeWindow;
import org.drools.serialization.protobuf.ProtobufMessages;
import org.drools.serialization.protobuf.ProtobufWorkingMemoryAction;

public class ProtobufBehaviorExpireWMAction extends SlidingTimeWindow.BehaviorExpireWMAction implements ProtobufWorkingMemoryAction {

    public ProtobufBehaviorExpireWMAction( MarshallerReaderContext context, ProtobufMessages.ActionQueue.Action action) {
        nodeId = action.getBehaviorExpire().getNodeId();
        WindowNode windowNode = (WindowNode) context.getSinks().get( nodeId );

        WindowNode.WindowMemory memory = context.getWorkingMemory().getNodeMemory( windowNode );

        Behavior.Context[] behaviorContext = memory.behaviorContext;

        int i = 0; //  <==== this needs fixing

        this.behavior = windowNode.getBehaviors()[i];
        this.context = behaviorContext[i];
    }

    @Override
    public ProtobufMessages.ActionQueue.Action serialize( MarshallerWriteContext outputCtx) {
        ProtobufMessages.ActionQueue.BehaviorExpire behaviorExpire = ProtobufMessages.ActionQueue.BehaviorExpire.newBuilder()
                .setNodeId( nodeId )
                .build();

        return ProtobufMessages.ActionQueue.Action.newBuilder()
                .setType( ProtobufMessages.ActionQueue.ActionType.BEHAVIOR_EXPIRE )
                .setBehaviorExpire( behaviorExpire )
                .build();

    }
}
