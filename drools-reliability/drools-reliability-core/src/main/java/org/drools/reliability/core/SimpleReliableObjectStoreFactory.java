package org.drools.reliability.core;

import org.drools.core.common.Storage;
import org.kie.api.internal.utils.KieService;

public interface SimpleReliableObjectStoreFactory extends KieService {

    SimpleReliableObjectStore createSimpleReliableObjectStore(Storage<Long, StoredObject> storage);

    class Holder {
        private static final SimpleReliableObjectStoreFactory INSTANCE = createInstance();

        static SimpleReliableObjectStoreFactory createInstance() {
            SimpleReliableObjectStoreFactory factory = KieService.load(SimpleReliableObjectStoreFactory.class );
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
