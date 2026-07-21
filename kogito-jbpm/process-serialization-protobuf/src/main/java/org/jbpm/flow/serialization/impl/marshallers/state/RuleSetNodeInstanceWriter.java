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
import org.jbpm.flow.serialization.protobuf.KogitoNodeInstanceContentsProtobuf.RuleSetNodeInstanceContent;
import org.jbpm.workflow.instance.node.RuleSetNodeInstance;
import org.kie.api.runtime.process.NodeInstance;

import com.google.protobuf.GeneratedMessageV3.Builder;

public class RuleSetNodeInstanceWriter implements NodeInstanceWriter {

    @Override
    public boolean accept(NodeInstance value) {
        return value instanceof RuleSetNodeInstance;
    }

    @Override
    public Builder<?> write(MarshallerWriterContext context, NodeInstance value) {
        RuleSetNodeInstance nodeInstance = (RuleSetNodeInstance) value;
        RuleSetNodeInstanceContent.Builder ruleSet = RuleSetNodeInstanceContent.newBuilder();
        ruleSet.setRuleFlowGroup(nodeInstance.getRuleFlowGroup());
        ruleSet.addAllTimerInstanceId(nodeInstance.getTimerInstances());
        if (nodeInstance.getTimerInstancesReference() != null) {
            ruleSet.putAllTimerInstanceReference(nodeInstance.getTimerInstancesReference());
        }

        return ruleSet;
    }

}
