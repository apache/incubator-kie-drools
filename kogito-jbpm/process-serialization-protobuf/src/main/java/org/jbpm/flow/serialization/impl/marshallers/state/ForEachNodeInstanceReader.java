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

import org.jbpm.flow.serialization.MarshallerReaderContext;
import org.jbpm.flow.serialization.NodeInstanceReader;
import org.jbpm.flow.serialization.ProcessInstanceMarshallerException;
import org.jbpm.flow.serialization.protobuf.KogitoNodeInstanceContentsProtobuf.ForEachNodeInstanceContent;
import org.jbpm.workflow.instance.node.ForEachNodeInstance;
import org.kie.api.runtime.process.NodeInstance;

import com.google.protobuf.Any;
import com.google.protobuf.GeneratedMessageV3;

public class ForEachNodeInstanceReader implements NodeInstanceReader {

    @Override
    public boolean accept(Any value) {
        return value.is(ForEachNodeInstanceContent.class);
    }

    @Override
    public NodeInstance read(MarshallerReaderContext context, Any value) {
        try {
            ForEachNodeInstanceContent content = value.unpack(ForEachNodeInstanceContent.class);
            ForEachNodeInstance nodeInstance = new ForEachNodeInstance();
            if (content.getTimerInstanceIdCount() > 0) {
                nodeInstance.internalSetTimerInstances(content.getTimerInstanceIdList().stream().toList());
            }
            if (content.getTimerInstanceReferenceCount() > 0) {
                nodeInstance.internalSetTimerInstancesReference(content.getTimerInstanceReferenceMap());
            }
            nodeInstance.setExecutedInstances(content.getExecutedInstances());
            nodeInstance.setTotalInstances(content.getTotalInstances());
            nodeInstance.setHasAsyncInstances(content.getHasAsyncInstances());
            return nodeInstance;
        } catch (Exception e) {
            throw new ProcessInstanceMarshallerException(e);
        }
    }

    @Override
    public Class<? extends GeneratedMessageV3> type() {
        return ForEachNodeInstanceContent.class;
    }
}
