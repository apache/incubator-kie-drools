package org.drools.reliability.core;

import org.drools.core.common.Storage;
import org.kie.api.internal.utils.KieService;
import org.kie.api.runtime.conf.PersistedSessionOption;

public interface SimpleReliableObjectStoreFactory extends KieService {

    SimpleReliableObjectStore createSimpleReliableObjectStore(Storage<Long, StoredObject> storage, PersistedSessionOption persistedSessionOption);

    class Holder {

        private static final SimpleReliableObjectStoreFactory INSTANCE = createInstance();

        private Holder() {
        }

        static SimpleReliableObjectStoreFactory createInstance() {
            SimpleReliableObjectStoreFactory factory = KieService.load(SimpleReliableObjectStoreFactory.class);
            if (factory == null) {
                return new SimpleSerializationReliableObjectStoreFactory();
            }
            return factory;
        }
    }

    static SimpleReliableObjectStoreFactory get() {
        return SimpleReliableObjectStoreFactory.Holder.INSTANCE;
    }
}
