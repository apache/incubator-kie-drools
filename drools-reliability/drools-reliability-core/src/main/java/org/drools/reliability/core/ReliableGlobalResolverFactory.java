package org.drools.reliability.core;

import org.drools.core.common.Storage;
import org.kie.api.internal.utils.KieService;

public interface ReliableGlobalResolverFactory extends KieService {

    ReliableGlobalResolver createReliableGlobalResolver(Storage<String, Object> storage);

    class Holder {

        private static final ReliableGlobalResolverFactory INSTANCE = createInstance();

        private Holder() {
        }

        static ReliableGlobalResolverFactory createInstance() {
            ReliableGlobalResolverFactory factory = KieService.load(ReliableGlobalResolverFactory.class);
            if (factory == null) {
                return new ReliableGlobalResolverFactoryImpl();
            }
            return factory;
        }
    }

    static ReliableGlobalResolverFactory get() {
        return ReliableGlobalResolverFactory.Holder.INSTANCE;
    }

    static class ReliableGlobalResolverFactoryImpl implements ReliableGlobalResolverFactory {

        static int servicePriorityValue = 0; // package access for test purposes

        @Override
        public ReliableGlobalResolver createReliableGlobalResolver(Storage<String, Object> storage) {
            return new ReliableGlobalResolver(storage);
        }

        @Override
        public int servicePriority() {
            return servicePriorityValue;
        }
    }
}
