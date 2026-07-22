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
package org.jbpm.workflow.instance.impl.factory;

import java.util.function.Supplier;

import org.jbpm.workflow.core.impl.NodeImpl;
import org.jbpm.workflow.instance.WorkflowProcessInstance;
import org.jbpm.workflow.instance.impl.NodeInstanceImpl;
import org.kie.api.definition.process.Node;
import org.kie.api.runtime.process.NodeInstance;
import org.kie.api.runtime.process.NodeInstanceContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SingletonNodeInstanceFactory extends AbstractNodeInstanceFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(SingletonNodeInstanceFactory.class);

    private Class<? extends NodeImpl> nodeDefinition;
    private Supplier<NodeInstanceImpl> nodeInstanceSupplier;

    @Override
    public Class<? extends Node> forClass() {
        return nodeDefinition;
    }

    public SingletonNodeInstanceFactory(Class<? extends NodeImpl> nodeDefinition, Supplier<NodeInstanceImpl> nodeInstanceSupplier) {
        this.nodeDefinition = nodeDefinition;
        this.nodeInstanceSupplier = nodeInstanceSupplier;
    }

    @Override
    public NodeInstance getNodeInstance(Node node, WorkflowProcessInstance processInstance, NodeInstanceContainer nodeInstanceContainer) {
        NodeInstance result = ((org.jbpm.workflow.instance.NodeInstanceContainer) nodeInstanceContainer).getFirstNodeInstance(node.getId());
        if (result != null) {
            return result;
        } else {
            LOGGER.debug("creating node {} with identifier {}", node, node.getId());
            return createInstance(nodeInstanceSupplier.get(), node, processInstance, nodeInstanceContainer);
        }
    }

}
