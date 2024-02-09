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
package org.drools.serialization.protobuf.timers;

import java.io.IOException;

import org.drools.core.marshalling.MarshallerReaderContext;
import org.drools.core.reteoo.WindowNode;
import org.drools.core.reteoo.WindowNode.WindowMemory;
import org.drools.serialization.protobuf.ProtobufMarshallerReaderContext;
import org.drools.serialization.protobuf.ProtobufMessages;
import org.drools.serialization.protobuf.TimersInputMarshaller;

public class BehaviorJobContextTimerInputMarshaller implements TimersInputMarshaller {

    public void read( ProtobufMarshallerReaderContext inCtx) throws IOException {
        int sinkId = inCtx.readInt();
        WindowNode windowNode = (WindowNode) inCtx.getSinks().get( sinkId );

        WindowMemory memory = inCtx.getWorkingMemory().getNodeMemory( windowNode );

        Object[] behaviorContext = memory.behaviorContext;

        int i = inCtx.readInt();
    }

    @Override
    public void deserialize( MarshallerReaderContext inCtx, ProtobufMessages.Timers.Timer timer) {
        long i = timer.getBehavior().getHandleId();
    }
}