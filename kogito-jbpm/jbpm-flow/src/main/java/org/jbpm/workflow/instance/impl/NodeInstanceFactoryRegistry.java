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
package org.jbpm.workflow.instance.impl;

import java.util.*;

import org.jbpm.util.JbpmClassLoaderUtil;
import org.kie.api.definition.process.Node;
import org.kie.api.runtime.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NodeInstanceFactoryRegistry {
    public static final String NODE_INSTANCE_FACTORY_REGISTRY_KEY = "NodeInstanceFactoryRegistry";

    private static final Logger LOGGER = LoggerFactory.getLogger(NodeInstanceFactoryRegistry.class);
    private static final NodeInstanceFactoryRegistry INSTANCE = new NodeInstanceFactoryRegistry();

    private static final Map<String, NodeInstanceFactoryRegistry> LOADED_NODE_INSTANCE_FACTORY_REGISTRIES = new HashMap<>();

    private final Map<Class<? extends Node>, NodeInstanceFactory> registry;

    public static NodeInstanceFactoryRegistry getInstance(Environment environment) {
        return getFactoryRegistryFromEnvironment(environment).orElse(INSTANCE);
    }

    private static Optional<NodeInstanceFactoryRegistry> getFactoryRegistryFromEnvironment(Environment environment) {
        if (environment != null && environment.get(NODE_INSTANCE_FACTORY_REGISTRY_KEY) != null) {
            String factoryRegistryClassName = (String) environment.get(NODE_INSTANCE_FACTORY_REGISTRY_KEY);
            return Optional.of(LOADED_NODE_INSTANCE_FACTORY_REGISTRIES.computeIfAbsent(factoryRegistryClassName,
                    className -> {
                        try {
                            return (NodeInstanceFactoryRegistry) Class.forName(className, true, JbpmClassLoaderUtil.findClassLoader()).getConstructor().newInstance();
                        } catch (Exception e) {
                            throw new RuntimeException("Could not create an instance of NodeInstanceFactoryRegistry with java type '" + className + "'", e);
                        }
                    }));
        }
        return Optional.empty();
    }

    protected NodeInstanceFactoryRegistry() {
        this.registry = new HashMap<>();
        initRegistry();
    }

    private void initRegistry() {
        ServiceLoader.load(NodeInstanceFactoryProvider.class, JbpmClassLoaderUtil.findClassLoader())
                .stream()
                .map(ServiceLoader.Provider::get)
                .map(NodeInstanceFactoryProvider::provide)
                .flatMap(Collection::stream)
                .forEach(this::register);
    }

    private void register(NodeInstanceFactory nodeInstanceFactory) {
        LOGGER.trace("registering new node instance factory for {} set by {}", nodeInstanceFactory.forClass(), nodeInstanceFactory.getClass().getCanonicalName());
        this.registry.put(nodeInstanceFactory.forClass(), nodeInstanceFactory);
    }

    public void register(Class<? extends Node> forClass, NodeInstanceFactory nodeInstanceFactory) {
        LOGGER.debug("override new node instance factory for {} set by {}", forClass, nodeInstanceFactory.getClass().getCanonicalName());
        this.registry.put(forClass, nodeInstanceFactory);
    }

    public NodeInstanceFactory getProcessNodeInstanceFactory(Node node) {
        Class<?> clazz = node.getClass();
        while (clazz != null) {
            NodeInstanceFactory result = this.get(clazz);
            if (result != null) {
                return result;
            }
            clazz = clazz.getSuperclass();
        }
        LOGGER.debug("node instance factory not found for node {}", node.getClass().getName());
        return null;
    }

    protected NodeInstanceFactory get(Class<?> clazz) {
        return this.registry.get(clazz);
    }

}
