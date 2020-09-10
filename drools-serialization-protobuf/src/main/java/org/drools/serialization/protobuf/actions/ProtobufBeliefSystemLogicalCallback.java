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

import org.drools.core.beliefsystem.simple.BeliefSystemLogicalCallback;
import org.drools.core.marshalling.impl.MarshallerReaderContext;
import org.drools.core.marshalling.impl.MarshallerWriteContext;
import org.drools.core.spi.Activation;
import org.drools.serialization.protobuf.PersisterHelper;
import org.drools.serialization.protobuf.ProtobufMessages;
import org.drools.serialization.protobuf.ProtobufWorkingMemoryAction;

public class ProtobufBeliefSystemLogicalCallback extends BeliefSystemLogicalCallback implements ProtobufWorkingMemoryAction {

    public ProtobufBeliefSystemLogicalCallback( MarshallerReaderContext context, ProtobufMessages.ActionQueue.Action _action) {
        ProtobufMessages.ActionQueue.LogicalRetract _retract = _action.getLogicalRetract();

        this.handle = context.getHandles().get( _retract.getHandleId() );
        this.activation = ( Activation ) context.getFilter()
                .getTuplesCache().get( PersisterHelper.createActivationKey(_retract.getActivation().getPackageName(),
                        _retract.getActivation().getRuleName(),
                        _retract.getActivation().getTuple()) ).getContextObject();
        this.context = this.activation.getPropagationContext();
        this.fullyRetract = _retract.getFullyRetract();
        this.update = _retract.getUpdate();
    }

    @Override
    public ProtobufMessages.ActionQueue.Action serialize( MarshallerWriteContext context) {
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
}
