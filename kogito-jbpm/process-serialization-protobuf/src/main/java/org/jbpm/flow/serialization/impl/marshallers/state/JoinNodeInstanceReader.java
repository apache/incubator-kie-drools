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

import java.util.HashMap;
import java.util.Map;

import org.jbpm.flow.serialization.MarshallerReaderContext;
import org.jbpm.flow.serialization.NodeInstanceReader;
import org.jbpm.flow.serialization.ProcessInstanceMarshallerException;
import org.jbpm.flow.serialization.protobuf.KogitoNodeInstanceContentsProtobuf.JoinNodeInstanceContent;
import org.jbpm.ruleflow.core.WorkflowElementIdentifierFactory;
import org.jbpm.workflow.instance.node.JoinInstance;
import org.kie.api.definition.process.WorkflowElementIdentifier;
import org.kie.api.runtime.process.NodeInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.Any;
import com.google.protobuf.GeneratedMessageV3;

public class JoinNodeInstanceReader implements NodeInstanceReader {

    private static Logger LOGGER = LoggerFactory.getLogger(JoinNodeInstanceReader.class);

    @Override
    public boolean accept(Any value) {
        return value.is(JoinNodeInstanceContent.class);
    }

    @Override
    public NodeInstance read(MarshallerReaderContext context, Any value) {
        try {
            JoinNodeInstanceContent content = value.unpack(JoinNodeInstanceContent.class);
            JoinInstance nodeInstance = new JoinInstance();
            if (content.getTriggerCount() > 0) {
                Map<WorkflowElementIdentifier, Integer> triggers = new HashMap<>();
                for (JoinNodeInstanceContent.JoinTrigger _join : content.getTriggerList()) {
                    LOGGER.debug("unmarshalling join {}", _join.getNodeId());
                    triggers.put(WorkflowElementIdentifierFactory.fromExternalFormat(_join.getNodeId()), _join.getCounter());
                }
                nodeInstance.internalSetTriggers(triggers);
            }
            return nodeInstance;
        } catch (Exception e) {
            throw new ProcessInstanceMarshallerException(e);
        }
    }

    @Override
    public Class<? extends GeneratedMessageV3> type() {
        return JoinNodeInstanceContent.class;
    }
}
