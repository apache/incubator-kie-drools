/*
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
package org.jbpm.flow.serialization.impl.marshallers.state;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jbpm.flow.serialization.MarshallerReaderContext;
import org.jbpm.flow.serialization.NodeInstanceReader;
import org.jbpm.flow.serialization.ProcessInstanceMarshallerException;
import org.jbpm.flow.serialization.protobuf.KogitoNodeInstanceContentsProtobuf.StateNodeInstanceContent;
import org.jbpm.workflow.instance.node.StateNodeInstance;
import org.kie.api.runtime.process.NodeInstance;

import com.google.protobuf.Any;
import com.google.protobuf.GeneratedMessageV3;

public class StateNodeInstanceReader implements NodeInstanceReader {

    @Override
    public Integer order() {
        return NodeInstanceReader.super.order() + 1;
    }

    @Override
    public boolean accept(Any value) {
        return value.is(StateNodeInstanceContent.class);
    }

    @Override
    public NodeInstance read(MarshallerReaderContext context, Any value) {
        try {
            StateNodeInstanceContent content = value.unpack(StateNodeInstanceContent.class);
            StateNodeInstance nodeInstance = new StateNodeInstance();
            if (content.getTimerInstanceIdCount() > 0) {
                List<String> timerInstances = new ArrayList<>();
                for (String _timerId : content.getTimerInstanceIdList()) {
                    timerInstances.add(_timerId);
                }
                nodeInstance.internalSetTimerInstances(timerInstances);
            }
            if (!content.getTimerInstanceReferenceMap().isEmpty()) {
                nodeInstance.internalSetTimerInstancesReference(new HashMap<>(content.getTimerInstanceReferenceMap()));
            }
            return nodeInstance;
        } catch (Exception e) {
            throw new ProcessInstanceMarshallerException(e);
        }
    }

    @Override
    public Class<? extends GeneratedMessageV3> type() {
        return StateNodeInstanceContent.class;
    }
}
