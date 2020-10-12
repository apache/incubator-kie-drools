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

package org.drools.serialization.protobuf.timers;

import org.drools.core.marshalling.impl.MarshallerReaderContext;
import org.drools.core.marshalling.impl.TupleKey;
import org.drools.core.phreak.PhreakTimerNode;
import org.drools.core.time.Trigger;
import org.drools.serialization.protobuf.PersisterHelper;
import org.drools.serialization.protobuf.ProtobufInputMarshaller;
import org.drools.serialization.protobuf.ProtobufMessages;
import org.drools.serialization.protobuf.TimersInputMarshaller;

public class TimerNodeTimerInputMarshaller implements TimersInputMarshaller {

    @Override
    public void deserialize( MarshallerReaderContext inCtx, ProtobufMessages.Timers.Timer _timer) {
        ProtobufMessages.Timers.TimerNodeTimer _tn = _timer.getTimerNode();

        int timerNodeId = _tn.getNodeId();
        TupleKey tuple = PersisterHelper.createTupleKey( _tn.getTuple() );
        Trigger trigger = ProtobufInputMarshaller.readTrigger( inCtx, _tn.getTrigger() );

        PhreakTimerNode.Scheduler scheduler = inCtx.removeTimerNodeScheduler( timerNodeId, tuple );
        if( scheduler != null ) {
            scheduler.schedule( trigger );
        }
    }
}
