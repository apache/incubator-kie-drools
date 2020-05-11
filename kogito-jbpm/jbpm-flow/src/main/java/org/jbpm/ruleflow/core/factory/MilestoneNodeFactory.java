/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.ruleflow.core.factory;

import org.jbpm.ruleflow.core.RuleFlowNodeContainerFactory;
import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.core.NodeContainer;
import org.jbpm.workflow.core.node.MilestoneNode;

public class MilestoneNodeFactory extends StateBasedNodeFactory {

    public static final String METHOD_CONSTRAINT = "constraint";
    public static final String METHOD_MATCH_VARIABLE = "matchVariable";

    public MilestoneNodeFactory(RuleFlowNodeContainerFactory nodeContainerFactory, NodeContainer nodeContainer, long id) {
        super(nodeContainerFactory, nodeContainer, id);
    }

    protected Node createNode() {
        return new MilestoneNode();
    }

    protected MilestoneNode getMilestoneNode() {
        return (MilestoneNode) getNode();
    }

    @Override
    public MilestoneNodeFactory name(String name) {
        super.name(name);
        return this;
    }

    @Override
    public MilestoneNodeFactory onEntryAction(String dialect, String action) {
        super.onEntryAction(dialect, action);
        return this;
    }

    @Override
    public MilestoneNodeFactory onExitAction(String dialect, String action) {
        super.onExitAction(dialect, action);
        return this;
    }

    @Override
    public MilestoneNodeFactory timer(String delay, String period, String dialect, String action) {
        super.timer(delay, period, dialect, action);
        return this;
    }

    public MilestoneNodeFactory matchVariable(String matchVariable) {
        getMilestoneNode().setMatchVariable(matchVariable);
        return this;
    }

    public MilestoneNodeFactory constraint(String constraint) {
        getMilestoneNode().setConstraint(constraint);
        return this;
    }
}
