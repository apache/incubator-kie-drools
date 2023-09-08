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
package org.kie.dmn.core.impl;

import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;

import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNMetadata;
import org.kie.dmn.api.core.FEELPropertyAccessible;
import org.kie.dmn.core.impl.DMNContextImpl.ScopeReference;

public class DMNContextFPAImpl implements DMNContext {

    private final FEELPropertyAccessible fpa;
    private Deque<ScopeReference> stack = new LinkedList<>();
    private DMNMetadataImpl metadata;

    public DMNContextFPAImpl(FEELPropertyAccessible bean) {
        this.fpa = bean;
        this.metadata = new DMNMetadataImpl();
    }

    @Override
    public Object set(String name, Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object get(String name) {
        return fpa.getFEELProperty(name).toOptional().orElse(null);
    }

    /**
     * Internal utility method
     * 
     * @return FEELPropertyAccessible which represents strongly typed context
     */
    public FEELPropertyAccessible getFpa() {
        return fpa;
    }

    private Map<String, Object> getCurrentEntries() {
        if (stack.isEmpty()) {
            return fpa.allFEELProperties();
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
        return this.metadata;
    }

    @Override
    public DMNContext clone() {
        DMNContextImpl newCtx = new DMNContextImpl(fpa.allFEELProperties(), metadata.asMap());
        for (ScopeReference e : stack) {
            newCtx.pushScope(e.getName(), e.getNamespace());
        }
        return newCtx;
    }
}
