package org.drools.reliability.test.example.h2mvstore;

import org.drools.reliability.test.util.TestConfigurationUtils;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.conf.PersistedSessionOption;

public class H2MVStoreStorageManagerExampleAfterFailOver {

    public static void main(String[] args) {
        System.setProperty(TestConfigurationUtils.DROOLS_RELIABILITY_MODULE_TEST, "H2MVSTORE");
        TestConfigurationUtils.configureServicePriorities();

        // Here we use the savedSessionId to retrieve the session. Explicitly 0 for now, but it could be different.
        KieSession session = H2MVStoreStorageManagerExample.getKieSession(PersistedSessionOption.fromSession(0).withPersistenceStrategy(PersistedSessionOption.PersistenceStrategy.STORES_ONLY));

        try {
            System.out.println("fireAllRules");
            session.fireAllRules();
            Object results = session.getGlobal("results");
            System.out.println("results = " + results);
        } finally {
            session.dispose();
        }
    }
}
