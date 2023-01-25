package org.drools.reliability;

import java.util.concurrent.locks.Lock;

import org.drools.core.common.ObjectStore;
import org.drools.kiesession.session.SessionComponentsFactory;

public class ReliableSessionComponentsFactory implements SessionComponentsFactory {

    public static final ReliabilityService reliabilityService = new ReliabilityService();

    @Override
    public ObjectStore createIdentityObjectStore() {
        return new ReliableObjectStore(this.reliabilityService.getCache());
    }

    @Override
    public ObjectStore createClassAwareObjectStore(boolean isEqualityBehaviour, Lock lock) {
        // When using reliability the only store available is the identity one
        return createIdentityObjectStore();
    }
}
