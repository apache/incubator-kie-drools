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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.function.Predicate;

import org.jbpm.ruleflow.core.RuleFlowNodeContainerFactory;
import org.jbpm.ruleflow.core.factory.NodeFactory;
import org.jbpm.workflow.core.NodeContainer;
import org.kie.api.definition.process.WorkflowElementIdentifier;

public class NodeFactoryProviderService {

    List<NodeFactoryProvider> providers;

    public NodeFactoryProviderService() {
        this.providers = new ArrayList<>();
        ServiceLoader.load(NodeFactoryProvider.class).forEach(this.providers::add);
    }

    public <T extends NodeFactory<T, P>, P extends RuleFlowNodeContainerFactory<P, ?>> T newNodeFactory(Class<?> type, P nodeContainerFactory, NodeContainer container, WorkflowElementIdentifier id) {
        Predicate<NodeFactoryProvider> typeFilter = provider -> provider.accept(type);
        Optional<NodeFactoryProvider> provider = providers.stream().filter(typeFilter).findAny();
        if (provider.isEmpty()) {
            throw new IllegalArgumentException("Provider for " + type.getCanonicalName() + " not found");
        }
        return provider.get().provide(nodeContainerFactory, container, id);
    }

}
