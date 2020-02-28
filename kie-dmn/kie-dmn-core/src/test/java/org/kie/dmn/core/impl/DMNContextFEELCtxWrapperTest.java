/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.dmn.core.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import org.junit.Test;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.api.feel.runtime.events.FEELEventListener;
import org.kie.dmn.core.BaseDMNContextTest;
import org.kie.dmn.feel.lang.EvaluationContext;

public class DMNContextFEELCtxWrapperTest extends BaseDMNContextTest {

    @Test
    public void testEmptyContext() {
        DMNContextFEELCtxWrapper ctx = new DMNContextFEELCtxWrapper(new EvaluationContextMock(Collections.emptyMap()));
        testCloneAndAlter(ctx, Collections.emptyMap(), Collections.emptyMap());
    }

    @Test
    public void testContextWithEntries() {
        DMNContextFEELCtxWrapper ctx = new DMNContextFEELCtxWrapper(new EvaluationContextMock(DEFAULT_ENTRIES));
        testCloneAndAlter(ctx, DEFAULT_ENTRIES, Collections.emptyMap());
    }

    @Test
    public void testContextWithEntriesAndMetadata() {
        DMNContextFEELCtxWrapper ctx = new DMNContextFEELCtxWrapper(new EvaluationContextMock(DEFAULT_ENTRIES), DEFAULT_METADATA);
        testCloneAndAlter(ctx, DEFAULT_ENTRIES, DEFAULT_METADATA);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testPushScopeException() {
        DMNContextFEELCtxWrapper ctx = new DMNContextFEELCtxWrapper(new EvaluationContextMock(Collections.emptyMap()));
        ctx.pushScope("scopeName", "scopeNamespace");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testPopScopeException() {
        DMNContextFEELCtxWrapper ctx = new DMNContextFEELCtxWrapper(new EvaluationContextMock(Collections.emptyMap()));
        ctx.popScope();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testScopeNamespaceException() {
        DMNContextFEELCtxWrapper ctx = new DMNContextFEELCtxWrapper(new EvaluationContextMock(Collections.emptyMap()));
        ctx.scopeNamespace();
    }

    private static class EvaluationContextMock implements EvaluationContext {
        private Map<String, Object> entries;

        public EvaluationContextMock(Map<String, Object> entries) {
            this.entries = new HashMap<>(entries);
        }

        // only these methods are required for the mock to behave as expected
        @Override
        public void setValue(String name, Object value) {
            entries.put(name, value);
        }

        @Override
        public Object getValue(String name) {
            return entries.get(name);
        }

        @Override
        public boolean isDefined(String name) {
            return entries.containsKey(name);
        }

        @Override
        public Map<String, Object> getAllValues() {
            return Collections.unmodifiableMap(entries);
        }

        // the rest is unneeded
        @Override
        public void enterFrame() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void exitFrame() {
            throw new UnsupportedOperationException();
        }

        @Override
        public EvaluationContext current() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Object getValue(String[] name) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isDefined(String[] name) {
            throw new UnsupportedOperationException();
        }

        @Override
        public DMNRuntime getDMNRuntime() {
            throw new UnsupportedOperationException();
        }

        @Override
        public ClassLoader getRootClassLoader() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void notifyEvt(Supplier<FEELEvent> event) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Collection<FEELEventListener> getListeners() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setRootObject(Object v) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Object getRootObject() {
            throw new UnsupportedOperationException();
        }
    }

}
