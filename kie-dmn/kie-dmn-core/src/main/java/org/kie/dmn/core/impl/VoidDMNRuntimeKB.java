package org.kie.dmn.core.impl;

import java.util.Collections;
import java.util.List;

import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.event.DMNRuntimeEventListener;
import org.kie.dmn.core.compiler.DMNProfile;

public class VoidDMNRuntimeKB implements DMNRuntimeKB {

    @Override
    public List<DMNModel> getModels() {
        return Collections.emptyList();
    }

    @Override
    public DMNModel getModel(String namespace, String modelName) {
        return null;
    }

    @Override
    public DMNModel getModelById(String namespace, String modelId) {
        return null;
    }

    @Override
    public List<DMNProfile> getProfiles() {
        return Collections.emptyList();
    }

    @Override
    public List<DMNRuntimeEventListener> getListeners() {
        return Collections.emptyList();
    }

    @Override
    public ClassLoader getRootClassLoader() {
        throw new UnsupportedOperationException();
    }

    @Override
    public InternalKnowledgeBase getInternalKnowledgeBase() {
        throw new UnsupportedOperationException();
    }

}
