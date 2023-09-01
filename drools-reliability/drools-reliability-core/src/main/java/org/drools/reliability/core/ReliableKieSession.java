package org.drools.reliability.core;

import org.drools.core.common.Storage;
import org.kie.api.runtime.KieSession;

public interface ReliableKieSession extends KieSession {
    void safepoint();

    Storage<String, Object> getActivationsStorage();

    void setActivationsStorage(Storage<String, Object> activationsStorage);
}
