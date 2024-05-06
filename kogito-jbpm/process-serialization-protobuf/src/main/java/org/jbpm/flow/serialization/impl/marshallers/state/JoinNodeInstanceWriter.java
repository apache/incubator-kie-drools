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
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.jbpm.flow.serialization.MarshallerWriterContext;
import org.jbpm.flow.serialization.NodeInstanceWriter;
import org.jbpm.flow.serialization.protobuf.KogitoNodeInstanceContentsProtobuf.JoinNodeInstanceContent;
import org.jbpm.flow.serialization.protobuf.KogitoNodeInstanceContentsProtobuf.JoinNodeInstanceContent.JoinTrigger;
import org.jbpm.workflow.instance.node.JoinInstance;
import org.kie.api.definition.process.WorkflowElementIdentifier;
import org.kie.api.runtime.process.NodeInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.GeneratedMessageV3.Builder;

public class JoinNodeInstanceWriter implements NodeInstanceWriter {

    private static final Logger LOGGER = LoggerFactory.getLogger(JoinNodeInstanceWriter.class);

    @Override
    public boolean accept(NodeInstance value) {
        return value instanceof JoinInstance;
    }

    @Override
    public Builder<?> write(MarshallerWriterContext context, NodeInstance value) {
        JoinInstance nodeInstance = (JoinInstance) value;
        JoinNodeInstanceContent.Builder joinBuilder = JoinNodeInstanceContent.newBuilder();
        Map<WorkflowElementIdentifier, Integer> triggers = nodeInstance.getTriggers();
        List<WorkflowElementIdentifier> keys = new ArrayList<>(triggers.keySet());
        Collections.sort(keys);

        for (WorkflowElementIdentifier key : keys) {
            LOGGER.info("marshalling join {}", key.toExternalFormat());
            joinBuilder.addTrigger(JoinTrigger.newBuilder()
                    .setNodeId(key.toExternalFormat())
                    .setCounter(triggers.get(key))
                    .build());
        }

        return joinBuilder;
    }

}
