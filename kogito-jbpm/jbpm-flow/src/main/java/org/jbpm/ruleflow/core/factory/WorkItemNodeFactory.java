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

import org.jbpm.process.core.ParameterDefinition;
import org.jbpm.process.core.Work;
import org.jbpm.process.core.context.variable.Mappable;
import org.jbpm.process.core.datatype.DataType;
import org.jbpm.process.core.impl.ParameterDefinitionImpl;
import org.jbpm.process.core.impl.WorkImpl;
import org.jbpm.ruleflow.core.RuleFlowNodeContainerFactory;
import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.core.NodeContainer;
import org.jbpm.workflow.core.node.WorkItemNode;

import java.util.Set;

public class WorkItemNodeFactory extends StateBasedNodeFactory implements MappableNodeFactory {

    public static final String METHOD_WORK_NAME = "workName";
    public static final String METHOD_WORK_PARAMETER = "workParameter";

    public WorkItemNodeFactory(RuleFlowNodeContainerFactory nodeContainerFactory, NodeContainer nodeContainer, long id) {
        super(nodeContainerFactory, nodeContainer, id);
    }

    protected Node createNode() {
        return new WorkItemNode();
    }

    protected WorkItemNode getWorkItemNode() {
        return (WorkItemNode) getNode();
    }

    @Override
    public WorkItemNodeFactory name(String name) {
        super.name(name);
        return this;
    }

    @Override
    public WorkItemNodeFactory onEntryAction(String dialect, String action) {
        super.onEntryAction(dialect, action);
        return this;
    }

    @Override
    public WorkItemNodeFactory onExitAction(String dialect, String action) {
        super.onExitAction(dialect, action);
        return this;
    }

    @Override
    public WorkItemNodeFactory timer(String delay, String period, String dialect, String action) {
        super.timer(delay, period, dialect, action);
        return this;
    }

    @Override
    public Mappable getMappableNode() {
        return getWorkItemNode();
    }

    @Override
    public WorkItemNodeFactory inMapping(String parameterName, String variableName) {
        MappableNodeFactory.super.inMapping(parameterName, variableName);
        return this;
    }

    @Override
    public WorkItemNodeFactory outMapping(String parameterName, String variableName) {
        MappableNodeFactory.super.outMapping(parameterName, variableName);
        return this;
    }

    public WorkItemNodeFactory waitForCompletion(boolean waitForCompletion) {
        getWorkItemNode().setWaitForCompletion(waitForCompletion);
        return this;
    }

    public WorkItemNodeFactory workName(String name) {
        Work work = getWorkItemNode().getWork();
        if (work == null) {
            work = new WorkImpl();
            getWorkItemNode().setWork(work);
        }
        work.setName(name);
        return this;
    }

    public WorkItemNodeFactory workParameter(String name, Object value) {
        Work work = getWorkItemNode().getWork();
        if (work == null) {
            work = new WorkImpl();
            getWorkItemNode().setWork(work);
        }
        work.setParameter(name, value);
        return this;
    }

    public WorkItemNodeFactory workParameterDefinition(String name, DataType dataType) {
        Work work = getWorkItemNode().getWork();
        if (work == null) {
            work = new WorkImpl();
            getWorkItemNode().setWork(work);
        }
        Set<ParameterDefinition> parameterDefinitions = work.getParameterDefinitions();
        parameterDefinitions.add(new ParameterDefinitionImpl(name, dataType));
        work.setParameterDefinitions(parameterDefinitions);
        return this;
    }
}
