/**
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
package org.kie.dmn.core.internal.utils;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;

import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNMetadata;
import org.kie.dmn.core.impl.DMNMetadataImpl;

public class MapBackedDMNContext implements DMNContext {

    private Deque<ScopeReference> stack = new LinkedList<>();
    private Map<String, Object> ctx;
    private DMNMetadata metadata;
    
    private MapBackedDMNContext() {
        // intentional
        ctx = new HashMap<>();
        metadata = new DMNMetadataImpl();
    }

    private MapBackedDMNContext(Map<String, Object> ctx) {
        // intentional
        this.ctx = new HashMap<>(ctx);
        this.metadata = new DMNMetadataImpl();
    }

    private MapBackedDMNContext(Map<String, Object> ctx, Map<String, Object> metadata) {
        // intentional
        this.ctx = new HashMap<>(ctx);
        this.metadata = new DMNMetadataImpl(metadata);
    }

    public static MapBackedDMNContext of(Map<String, Object> ctx) {
        return new MapBackedDMNContext(ctx);
    }

    public static MapBackedDMNContext of(Map<String, Object> ctx, Map<String, Object> metadata) {
        return new MapBackedDMNContext(ctx, metadata);
    }

    @Override
    public DMNContext clone() {
        return of(this.ctx, this.metadata.asMap());
    }

    @Override
    public Object set(String name, Object value) {
        return getCurrentEntries().put(name, value);
    }

    @Override
    public Object get(String name) {
        return getCurrentEntries().get(name);
    }

    private Map<String, Object> getCurrentEntries() {
        if (stack.isEmpty()) {
            return ctx;
        } else {
            return stack.peek().getRef(); // Intentional, symbol resolution in scope should limit at the top of the stack (for DMN semantic).
        }
    }

    @Override
    public void pushScope(String name, String namespace) {
        Map<String, Object> scopeRef = (Map<String, Object>) getCurrentEntries().computeIfAbsent(name, s -> new LinkedHashMap<String, Object>());
        stack.push(new ScopeReference(name, namespace, scopeRef));
    }

    @Override
    public void popScope() {
        stack.pop();
    }

    @Override
    public Optional<String> scopeNamespace() {
        if (stack.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(stack.peek().getNamespace());
        }
    }

    @Override
    public Map<String, Object> getAll() {
        return getCurrentEntries();
    }

    @Override
    public boolean isDefined(String name) {
        return getCurrentEntries().containsKey(name);
    }

    @Override
    public DMNMetadata getMetadata() {
        return metadata;
    }

    public static class ScopeReference {

        private final String name;
        private final String namespace;
        private final Map<String, Object> ref;

        public ScopeReference(String name, String namespace, Map<String, Object> ref) {
            super();
            this.name = name;
            this.namespace = namespace;
            this.ref = ref;
        }

        public String getName() {
            return name;
        }

        public String getNamespace() {
            return namespace;
        }

        public Map<String, Object> getRef() {
            return ref;
        }

    }
}