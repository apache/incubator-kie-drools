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
