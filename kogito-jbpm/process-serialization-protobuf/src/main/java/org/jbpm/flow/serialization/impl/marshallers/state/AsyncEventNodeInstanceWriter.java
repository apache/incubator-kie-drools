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

import org.jbpm.flow.serialization.MarshallerWriterContext;
import org.jbpm.flow.serialization.NodeInstanceWriter;
import org.jbpm.flow.serialization.protobuf.KogitoNodeInstanceContentsProtobuf.AsyncEventNodeInstanceContent;
import org.jbpm.workflow.core.node.AsyncEventNodeInstance;
import org.kie.api.runtime.process.NodeInstance;

import com.google.protobuf.GeneratedMessageV3.Builder;

public class AsyncEventNodeInstanceWriter implements NodeInstanceWriter {

    @Override
    public boolean accept(NodeInstance value) {
        return value instanceof AsyncEventNodeInstance;
    }

    @Override
    public Builder<?> write(MarshallerWriterContext context, NodeInstance value) {
        AsyncEventNodeInstance nodeInstance = (AsyncEventNodeInstance) value;
        AsyncEventNodeInstanceContent.Builder builder = AsyncEventNodeInstanceContent.newBuilder();
        if (nodeInstance.getJobId() != null) {
            builder.setJobId(nodeInstance.getJobId());
        }
        return builder;
    }

}
