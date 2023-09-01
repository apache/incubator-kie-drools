package org.kie.dmn.core.impl;

import java.util.List;

import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.event.DMNRuntimeEventListener;
import org.kie.dmn.core.compiler.DMNProfile;

public interface DMNRuntimeKB {

    List<DMNModel> getModels();

    DMNModel getModel(String namespace, String modelName);

    DMNModel getModelById(String namespace, String modelId);

    List<DMNProfile> getProfiles();

    List<DMNRuntimeEventListener> getListeners();

    /**
     * @throws UnsupportedOperationException if not supported on this platform.
     */
    ClassLoader getRootClassLoader();

    /**
     * @throws UnsupportedOperationException if not supported on this platform.
     */
    InternalKnowledgeBase getInternalKnowledgeBase();
}
