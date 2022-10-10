package org.drools.model;

import java.util.Map;
import java.util.ServiceLoader;

public interface PrototypeFactFactory {

    PrototypeFact createMapBasedFact(Prototype prototype);

    PrototypeFact createMapBasedFact(Prototype prototype, Map<String, Object> valuesMap);

    static PrototypeFactFactory get() {
        return Factory.get();
    }

    /**
     * A Factory for this KieServices
     */
    class Factory {

        private static class LazyHolder {
            private static PrototypeFactFactory INSTANCE = ServiceLoader.load(PrototypeFactFactory.class)
                    .findFirst().orElseThrow(() -> new RuntimeException("Unable to fine PrototypeFactFactory service, is drools-model-compiler on the classpath?"));
        }

        /**
         * Returns a reference to the KieServices singleton
         */
        public static PrototypeFactFactory get() {
            return LazyHolder.INSTANCE;
        }
    }
}