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
package org.jbpm.compiler.canonical.node;

import java.util.Map;
import java.util.ServiceLoader;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.jbpm.compiler.canonical.AbstractNodeVisitor;
import org.jbpm.util.JbpmClassLoaderUtil;

public class NodeVisitorBuilderService {

    private Map<Class<?>, NodeVisitorBuilder> nodesVisitors;
    private ClassLoader contextClassLoader;

    public NodeVisitorBuilderService() {
        this(JbpmClassLoaderUtil.findClassLoader());
    }

    public NodeVisitorBuilderService(ClassLoader contextClassLoader) {
        this.nodesVisitors = ServiceLoader.load(NodeVisitorBuilder.class).stream().map(ServiceLoader.Provider::get).collect(Collectors.toMap(NodeVisitorBuilder::type, Function.identity()));
        this.contextClassLoader = contextClassLoader;
    }

    public AbstractNodeVisitor<? extends org.kie.api.definition.process.Node> findNodeVisitor(Class<?> clazz) {
        NodeVisitorBuilder nodeVisitor = nodesVisitors.get(clazz);
        if (nodeVisitor != null) {
            return nodeVisitor.visitor(this, contextClassLoader);
        }
        throw new IllegalArgumentException(clazz + " visitor not supported");
    }

}
