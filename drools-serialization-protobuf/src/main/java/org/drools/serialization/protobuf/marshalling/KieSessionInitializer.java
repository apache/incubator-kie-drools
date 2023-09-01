package org.drools.serialization.protobuf.marshalling;

import org.kie.api.runtime.KieSession;

public interface KieSessionInitializer {
    void init(KieSession kSession);
}
