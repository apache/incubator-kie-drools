package org.kie.api.runtime;

import org.kie.api.KieBase;

public interface KieContext {
    KieRuntime getKieRuntime();

    default KieBase getKieBase() {
        return getKieRuntime().getKieBase();
    }

    /**
     * Added for backwards compatibility.
     * Will be removed in the future.
     */
    @Deprecated
    KieRuntime getKnowledgeRuntime();
}
