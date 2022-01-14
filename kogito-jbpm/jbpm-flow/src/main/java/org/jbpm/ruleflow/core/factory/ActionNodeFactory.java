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

import org.jbpm.process.instance.impl.Action;
import org.jbpm.ruleflow.core.RuleFlowNodeContainerFactory;
import org.jbpm.workflow.core.DroolsAction;
import org.jbpm.workflow.core.NodeContainer;
import org.jbpm.workflow.core.impl.DroolsConsequenceAction;
import org.jbpm.workflow.core.node.ActionNode;

public class ActionNodeFactory<T extends RuleFlowNodeContainerFactory<T, ?>> extends NodeFactory<ActionNodeFactory<T>, T> implements SupportsAction<ActionNodeFactory<T>, T> {

    public static final String METHOD_ACTION = "action";

    public ActionNodeFactory(T nodeContainerFactory,
            NodeContainer nodeContainer,
            long id) {
        super(nodeContainerFactory, nodeContainer, new ActionNode(), id);
    }

    protected ActionNode getActionNode() {
        return (ActionNode) getNode();
    }

    @Override
    public ActionNodeFactory<T> name(String name) {
        getNode().setName(name);
        return this;
    }

    public ActionNodeFactory<T> action(String dialect,
            String action) {
        return action(dialect, action, false);
    }

    public ActionNodeFactory<T> action(String dialect,
            String action,
            boolean isDroolsAction) {
        if (isDroolsAction) {
            DroolsAction droolsAction = new DroolsAction();
            droolsAction.setMetaData("Action", action);
            getActionNode().setAction(droolsAction);
        } else {
            getActionNode().setAction(new DroolsConsequenceAction(dialect, action));
        }
        return this;
    }

    @Override
    public ActionNodeFactory<T> action(Action action) {
        DroolsAction droolsAction = new DroolsAction();
        droolsAction.setMetaData("Action", action);
        getActionNode().setAction(droolsAction);
        return this;
    }
}
