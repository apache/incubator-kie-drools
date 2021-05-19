/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.ruleflow.core.factory;

import java.util.function.Predicate;

import org.jbpm.ruleflow.core.RuleFlowNodeContainerFactory;
import org.jbpm.workflow.core.NodeContainer;
import org.jbpm.workflow.core.node.MilestoneNode;
import org.kie.api.runtime.process.ProcessContext;

public class MilestoneNodeFactory<T extends RuleFlowNodeContainerFactory<T, ?>> extends StateBasedNodeFactory<MilestoneNodeFactory<T>, T> {

    public static final String METHOD_CONDITION = "condition";

    public MilestoneNodeFactory(T nodeContainerFactory, NodeContainer nodeContainer, long id) {
        super(nodeContainerFactory, nodeContainer, new MilestoneNode(), id);
    }

    protected MilestoneNode getMilestoneNode() {
        return (MilestoneNode) getNode();
    }

    public MilestoneNodeFactory<T> condition(Predicate<ProcessContext> condition) {
        getMilestoneNode().setCondition(condition);
        return this;
    }
}
