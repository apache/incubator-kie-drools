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

import java.util.Map;
import java.util.Optional;

import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNMetadata;
import org.kie.dmn.feel.lang.EvaluationContext;

public class DMNContextFEELCtxWrapper implements DMNContext {

    private EvaluationContext wrapped;
    private DMNMetadata metadata;

    public DMNContextFEELCtxWrapper(EvaluationContext wrapped) {
        this.wrapped = wrapped;
        this.metadata = new DMNMetadataImpl();
    }

    public DMNContextFEELCtxWrapper(EvaluationContext wrapped, Map<String, Object> metadata) {
        this.wrapped = wrapped;
        this.metadata = new DMNMetadataImpl(metadata);
    }

    public void enterFrame() {
        wrapped.enterFrame();
    }

    public void exitFrame() {
        wrapped.exitFrame();
    }

    @Override
    public Object set(String name, Object value) {
        Object previous = wrapped.getValue(name);
        wrapped.setValue(name, value);
        return previous;
    }

    @Override
    public Object get(String name) {
        return wrapped.getValue(name);
    }

    @Override
    public Map<String, Object> getAll() {
        return wrapped.getAllValues();
    }

    @Override
    public boolean isDefined(String name) {
        return wrapped.isDefined(name);
    }

    @Override
    public DMNMetadata getMetadata() {
        return metadata;
    }

    @Override
    public DMNContext clone() {
        return new DMNContextImpl(wrapped.getAllValues(), metadata.asMap());
    }

    @Override
    public void pushScope(String name, String namespace) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void popScope() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<String> scopeNamespace() {
        throw new UnsupportedOperationException();
    }

}
