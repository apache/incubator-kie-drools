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
package org.jbpm.ruleflow.core.factory.provider;

import org.jbpm.ruleflow.core.RuleFlowNodeContainerFactory;
import org.jbpm.ruleflow.core.factory.NodeFactory;
import org.jbpm.ruleflow.core.factory.TimerNodeFactory;
import org.jbpm.workflow.core.NodeContainer;
import org.jbpm.workflow.core.node.TimerNode;
import org.kie.api.definition.process.WorkflowElementIdentifier;

public class TimerNodeFactoryProvider implements NodeFactoryProvider {

    @Override
    public boolean accept(Class<?> type) {
        return TimerNode.class.equals(type);
    }

    @Override
    public <R extends NodeFactory<R, P>, P extends RuleFlowNodeContainerFactory<P, ?>> R provide(P nodeContainerFactory, NodeContainer container, WorkflowElementIdentifier id) {
        return (R) new TimerNodeFactory<P>(nodeContainerFactory, container, id);
    }

}
