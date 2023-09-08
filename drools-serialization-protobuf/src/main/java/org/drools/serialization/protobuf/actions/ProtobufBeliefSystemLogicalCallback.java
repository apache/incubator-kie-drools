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
