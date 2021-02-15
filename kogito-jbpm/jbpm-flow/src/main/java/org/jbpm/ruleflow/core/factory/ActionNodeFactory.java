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
import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.core.NodeContainer;
import org.jbpm.workflow.core.impl.DroolsConsequenceAction;
import org.jbpm.workflow.core.node.ActionNode;

import static org.jbpm.ruleflow.core.Metadata.ACTION;

public class ActionNodeFactory extends ExtendedNodeFactory {

    public static final String METHOD_ACTION = "action";

    public ActionNodeFactory(RuleFlowNodeContainerFactory nodeContainerFactory, NodeContainer nodeContainer, long id) {
        super(nodeContainerFactory, nodeContainer, id);
    }

    protected Node createNode() {
        return new ActionNode();
    }

    protected ActionNode getActionNode() {
        return (ActionNode) getNode();
    }

    @Override
    public ActionNodeFactory name(String name) {
        super.name(name);
        return this;
    }

    public ActionNodeFactory action(String dialect, String action) {
        return action(dialect, action, false);
    }

    public ActionNodeFactory action(String dialect,
                                    String action,
                                    boolean isDroolsAction) {
        if (isDroolsAction) {
            DroolsAction droolsAction = new DroolsAction();
            droolsAction.setMetaData(ACTION, action);
            getActionNode().setAction(droolsAction);
        } else {
            getActionNode().setAction(new DroolsConsequenceAction(dialect, action));
        }
        return this;
    }

    public ActionNodeFactory action(Action action) {
        DroolsAction droolsAction = new DroolsAction();
        droolsAction.setMetaData(ACTION, action);
        getActionNode().setAction(droolsAction);
        return this;
    }
}
