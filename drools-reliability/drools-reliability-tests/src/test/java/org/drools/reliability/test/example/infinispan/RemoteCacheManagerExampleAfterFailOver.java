package org.drools.reliability.test.example.infinispan;

import org.drools.reliability.test.util.TestConfigurationUtils;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.conf.PersistedSessionOption;

/**
 * Example class to demonstrate how to use RemoteCacheManager.
 * <p>
 * See RemoteCacheManagerExample for more details.
 */
public class RemoteCacheManagerExampleAfterFailOver {

    public static void main(String[] args) {
        System.setProperty(TestConfigurationUtils.DROOLS_RELIABILITY_MODULE_TEST, "INFINISPAN");
        TestConfigurationUtils.configureServicePriorities();

        // Here we use the savedSessionId to retrieve the session. Explicitly 0 for now, but it could be different.
        KieSession session = RemoteCacheManagerExample.getKieSession(PersistedSessionOption.fromSession(0).withPersistenceStrategy(PersistedSessionOption.PersistenceStrategy.STORES_ONLY));

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
