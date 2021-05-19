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

import org.jbpm.ruleflow.core.RuleFlowNodeContainerFactory;
import org.jbpm.workflow.core.NodeContainer;
import org.jbpm.workflow.core.node.Join;

public class JoinFactory<T extends RuleFlowNodeContainerFactory<T, ?>> extends NodeFactory<JoinFactory<T>, T> {

    public static final String METHOD_TYPE = "type";

    public JoinFactory(T nodeContainerFactory, NodeContainer nodeContainer, long id) {
        super(nodeContainerFactory, nodeContainer, new Join(), id);
    }

    protected Join getJoin() {
        return (Join) getNode();
    }

    @Override
    public JoinFactory<T> name(String name) {
        super.name(name);
        return this;
    }

    public JoinFactory<T> type(int type) {
        getJoin().setType(type);
        return this;
    }

    public JoinFactory<T> type(String n) {
        getJoin().setN(n);
        return this;
    }

}
