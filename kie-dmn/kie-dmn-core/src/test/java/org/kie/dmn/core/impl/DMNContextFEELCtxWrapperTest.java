package org.kie.dmn.core.impl;

import org.junit.Test;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.api.feel.runtime.events.FEELEventListener;
import org.kie.dmn.core.BaseDMNContextTest;
import org.kie.dmn.feel.lang.EvaluationContext;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class DMNContextFEELCtxWrapperTest extends BaseDMNContextTest {

    @Test
    public void testEmptyContext() {
        DMNContextFEELCtxWrapper ctx = new DMNContextFEELCtxWrapper(mockedEvaluationContext(Collections.emptyMap()));
        testCloneAndAlter(ctx, Collections.emptyMap(), Collections.emptyMap());
    }

    @Test
    public void testContextWithEntries() {
        DMNContextFEELCtxWrapper ctx = new DMNContextFEELCtxWrapper(mockedEvaluationContext(DEFAULT_ENTRIES));
        testCloneAndAlter(ctx, DEFAULT_ENTRIES, Collections.emptyMap());
    }

    @Test
    public void testContextWithEntriesAndMetadata() {
        DMNContextFEELCtxWrapper ctx = new DMNContextFEELCtxWrapper(mockedEvaluationContext(DEFAULT_ENTRIES), DEFAULT_METADATA);
        testCloneAndAlter(ctx, DEFAULT_ENTRIES, DEFAULT_METADATA);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testPushScopeException() {
        DMNContextFEELCtxWrapper ctx = new DMNContextFEELCtxWrapper(mockedEvaluationContext(Collections.emptyMap()));
        ctx.pushScope("scopeName", "scopeNamespace");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testPopScopeException() {
        DMNContextFEELCtxWrapper ctx = new DMNContextFEELCtxWrapper(mockedEvaluationContext(Collections.emptyMap()));
        ctx.popScope();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testScopeNamespaceException() {
        DMNContextFEELCtxWrapper ctx = new DMNContextFEELCtxWrapper(mockedEvaluationContext(Collections.emptyMap()));
        ctx.scopeNamespace();
    }

    private static EvaluationContext mockedEvaluationContext(Map<String, Object> expectedEntries) {
        return new EvaluationContext() {
            private Map<String, Object> entries = new HashMap<>(expectedEntries);

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
            public void setValue(String name, Object value) {
                entries.put(name, value);
            }

            @Override
            public Object getValue(String name) {
                return entries.get(name);
            }

            @Override
            public Object getValue(String[] name) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean isDefined(String name) {
                return entries.containsKey(name);
            }

            @Override
            public boolean isDefined(String[] name) {
                throw new UnsupportedOperationException();
            }

            @Override
            public Map<String, Object> getAllValues() {
                return Collections.unmodifiableMap(entries);
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
        };
    }

}
