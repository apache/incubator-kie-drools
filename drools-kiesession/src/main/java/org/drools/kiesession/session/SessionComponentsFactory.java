package org.drools.kiesession.session;

import java.util.concurrent.locks.Lock;

import org.drools.core.common.ClassAwareObjectStore;
import org.drools.core.common.IdentityObjectStore;
import org.drools.core.common.ObjectStore;
import org.kie.api.internal.utils.KieService;

public interface SessionComponentsFactory extends KieService {
    class Holder {
        private static final SessionComponentsFactory INSTANCE = createInstance();

        static SessionComponentsFactory createInstance() {
            SessionComponentsFactory factory = KieService.load( SessionComponentsFactory.class );
            return factory == null ? DefaultSessionComponentsFactory.INSTANCE : factory;
        }
    }

    static SessionComponentsFactory get() {
        return SessionComponentsFactory.Holder.INSTANCE;
    }

    ObjectStore createIdentityObjectStore(String entryPointName);

    ObjectStore createClassAwareObjectStore(String entryPointName, boolean isEqualityBehaviour, Lock lock);


    class DefaultSessionComponentsFactory implements SessionComponentsFactory {
        private static final DefaultSessionComponentsFactory INSTANCE = new DefaultSessionComponentsFactory();

        @Override
        public ObjectStore createIdentityObjectStore(String entryPointName) {
            return new IdentityObjectStore();
        }

        @Override
        public ObjectStore createClassAwareObjectStore(String entryPointName, boolean isEqualityBehaviour, Lock lock) {
            return new ClassAwareObjectStore(isEqualityBehaviour, lock);
        }
    }
}
